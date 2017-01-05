
#if defined(__WIN32__) || defined(_WIN32) || defined(_WIN32_WCE)
/* Windows */
#if defined(WINNT) || defined(_WINNT) || defined(__BORLANDC__) || defined(__MINGW32__) || defined(_WIN32_WCE) || defined (_MSC_VER)
#define _MSWSOCK_
#define FD_SETSIZE      1024
#include <winsock2.h>
#include <ws2tcpip.h>
#endif
#include <windows.h>
#include <errno.h>
#include <string.h>

#define closeSocket closesocket
#ifdef EWOULDBLOCK
#undef EWOULDBLOCK
#endif
#ifdef EINPROGRESS
#undef EINPROGRESS
#endif
#ifdef EAGAIN
#undef EAGAIN
#endif
#ifdef EINTR
#undef EINTR
#endif
#define EWOULDBLOCK WSAEWOULDBLOCK
#define EINPROGRESS WSAEWOULDBLOCK
#define EAGAIN WSAEWOULDBLOCK
#define EINTR WSAEINTR

#else
/* Unix */


#include <fcntl.h>
//#include <error.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <strings.h>
#include <ctype.h>
#include <stdint.h>
#if defined(_QNX4)
#include <sys/select.h>
#include <unix.h>

#endif

#define closeSocket close
#define SOCKET_ERROR -1
#endif

#include <stdio.h>
#include <stdlib.h>
#include <time.h>

//////////////////////////////////////////////////////////////////////////
#ifdef _USE_JSONCPP_LIB
#include <json/json.h>
using namespace  std;
#endif
#include "VzClientSDK_LPDefine.h"
#include "tools.h"
#include "tcpclient.h"
#include <android/log.h>

typedef struct
{
	unsigned char magic[2];
	unsigned char type;
	unsigned char reserved;
	unsigned int length;
}BLOCK_HEADER;

typedef struct{
	char *cameraIp;
	int socket;
}CAMERA_INFO;

//////////////////////////////////////////////////////////////////////////
const int port=8131;
const char szIp[32]="192.168.1.28";
int confidenceLevel = 65;//默认值为95 20160328改成65
int keepalive = 8;
ResultCallBack g_pFunc;
KeepAliveCallBack g_pKeepFunc;
CAMERA_INFO cameraInfo[5];
enum
{
	TCP_BLOCK = 0,
	TCP_UNBLOCK
};
enum
{
	DISABLE_PUSH = 0,
	ENABLE_PUSH
};
enum
{
	FORMAT_BINARY = 0,
	FORMAT_JSON
};
enum
{
	DISABLE_IMAGE = 0,
	ENABLE_IMAGE
};
enum
{
	IO_OUT1 = 0,
	IO_OUT2,
	IO_OUT3,
	IO_OUT4
};
enum
{
	IO_OUT_LOW = 0,
	IO_OUT_HIGH,
	IO_OUT_SQUARE_HIGH //开闸
};



//const int port=5298;
//const char szIp[32]="192.168.1.225";
//cmd json str
// get serial number
const char getsn[] = "{"
						"\"cmd\" : \"getsn\""
					"}";
//enable image transformation from the ivs result
const char enable[] = "{"
						"\"cmd\" : \"ivsresult\","
						"\"image\" : true"
					"}";
//disable image transformation from the ivs result
const char disable[] = "{"
						"\"cmd\" : \"ivsresult\","
						"\"image\" : false"
					"}";
const char getivsresult[] = "{"
						"\"cmd\" : \"getivsresult\","
						"\"image\" : true"
					"}";
static const char offLineRegister[] ="{\n"
						"\"cmd\":\"offline\",\n"
						"\"interval\":2\n"
					"}";
static const char offLineNotify[] =		"{\n"
						"\"cmd\":\"offline_notify\",\n"
						"\"id\":2\n"
						"}";
static const char triggerIVSResult[] =		"{\n"
						"\"cmd\":\"trigger\"\n"
						"}";
#if defined(__WIN32__) || defined(_WIN32)
#define WS_VERSION_CHOICE1 0x202/*MAKEWORD(2,2)*/
#define WS_VERSION_CHOICE2 0x101/*MAKEWORD(1,1)*/
int initializeWinsockIfNecessary(void)
{
	static int _haveInitializedWinsock = 0;
	WSADATA	wsadata;

	if (!_haveInitializedWinsock) 
	{
		if ((WSAStartup(WS_VERSION_CHOICE1, &wsadata) != 0)
		    && ((WSAStartup(WS_VERSION_CHOICE2, &wsadata)) != 0)) 
		{
			return 0; /* error in initialization */
		}
    	if ((wsadata.wVersion != WS_VERSION_CHOICE1)
    	    && (wsadata.wVersion != WS_VERSION_CHOICE2)) 
		{
	        	WSACleanup();
				return 0; /* desired Winsock version was not available */
		}
		_haveInitializedWinsock = 1;
	}

	return 1;
}
int releaseWinsockIfNecessary(void)
{
	WSACleanup();
	return 1;
}

#else
#define SOCKET int
#define  INVALID_SOCKET -1
int initializeWinsockIfNecessary(void) { return 1; }
int releaseWinsockIfNecessary(void){ return 1; }
#endif
int makeSocketNonBlocking(int sock) {
#if defined(__WIN32__) || defined(_WIN32)
	unsigned long arg = 1;
	return ioctlsocket(sock, FIONBIO, &arg) == 0;
#else
	int curFlags = fcntl(sock, F_GETFL, 0);
	return fcntl(sock, F_SETFL, curFlags|O_NONBLOCK) >= 0;
#endif
}

int makeSocketBlocking(int sock) {
#if defined(__WIN32__) || defined(_WIN32)
	unsigned long arg = 0;
	return ioctlsocket(sock, FIONBIO, &arg) == 0;
#else
	int curFlags = fcntl(sock, F_GETFL, 0);
	return fcntl(sock, F_SETFL, curFlags&(~O_NONBLOCK)) >= 0;
#endif
}
int getLastError()
{
#if defined(__WIN32__) || defined(_WIN32)
	return WSAGetLastError();
#else
	return errno;
#endif
}
SOCKET g_client;

enum
{
	BLOCK_TYPE_JSON_RESULT = 0,
	BLOCK_TYPE_BIN_RESULT,
	BLOCK_TYPE_IMAGE_DATA 
};

int sendCmd(SOCKET s,const char cmd[])
{
	LOGI("sendCmd = %s", cmd);
	int len = strlen(cmd)+1;
	char buff[8] = {0};
	buff[0] = 'V';
	buff[1] = 'Z';
	int nlen = htonl(len);
	memcpy(&buff[4], &nlen, 4);
	
	if (send(s, buff,8,0) == 8)
	{
		LOGI("sendsucess&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		return send(s,cmd,len,0);
	}
	else
	{
		return 0;
	}	
}

int sendKeepAlive(SOCKET s)
{
	char buff[8]={0};
	buff[0] = 'V';
	buff[1] = 'Z';
	buff[2] = 1;
	return send(s, buff, 8, 0);
}

int recvPacketSize(SOCKET s)
{
	char header[8] = {0};
	int len = 0;
	//确保收到8个字节

	len = recv(s, header, 8 , 0);
	if ( SOCKET_ERROR == len)
	{
		LOGI("recv errro@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		return -1;
	}


	if ('V' == header[0] && 'Z' == header[1])
	{
		LOGI("VZVZVZVZVZVZVZVZVZVZVZVZVZVZVZVZVZVZVZZZZZZZZZZZZZZZZ");
		int toRecvLen = 0;
		memcpy(&toRecvLen, &header[4], 4);
		return header[2]==1? 0: htonl(toRecvLen);
	}
	int RecvLen = 0;
	memcpy(&RecvLen, &header[4], 4);
	LOGI("recvlen = %d", htonl(RecvLen));
	LOGI("%s, %d:: len = %d", __FUNCTION__, __LINE__,len);
	LOGI("recv errro!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	return -1;
}

int recvPacket(SOCKET s,char buff[],int len)
{
	int toRecvLen = recvPacketSize(s);
	
	if (toRecvLen > 0)
	{
		return recv(s,buff,toRecvLen>len?len:toRecvLen,0);
	}
	else if(toRecvLen < 0)
	{
		//socket error
		return -1;
	}
	return 0;

}

void saveImage(char buff[], int size,char fileName[260])
{	
	FILE* file;
	char secFilename[260];
	if (NULL == fileName)
	{
		sprintf(secFilename,"%ld.jpg",time(NULL));
		file= fopen(secFilename ,"wb");
	}
	else
	{
		file= fopen(fileName ,"wb");
	}
	
	if (NULL == file)
	{
		perror("create image file failed!");
		return ;
	}
	if (fwrite(buff,1,size,file) == size)
	{
		printf("save image file: %s!\n", secFilename);
	}
	
	fclose(file);
}
#ifdef _USE_JSON_LIB
void saveForTest(char recvBuffer[],int len)
{
	char lpr[64] = {0};
	time_t sec = 0;

	try {
		Json::Reader reader;
		Json::Value root;
		bool parsingSuccessful = reader.parse(recvBuffer, root);
		if (!parsingSuccessful) {
			printf("Failed to parse file: \n%s\n",				
				reader.getFormattedErrorMessages().c_str());			
		}
		strcpy(lpr, root["PlateResult"]["license"].asCString());
		sec = root["PlateResult"]["timeStamp"]["Timeval"]["sec"].asUInt();
	}
	catch (const std::exception &e) {
		printf("Unhandled exception:\n%s\n", e.what());
	}	
	if (lpr[0] == 0)
	{
		strcpy(lpr,"none");
	}
	if (sec == 0)
	{
		strcat(lpr," - no time");
		sec = time(NULL);
	}

	char imageFileName[_MAX_FNAME];
	struct tm *p = localtime(&sec);
	sprintf(imageFileName,"%s - [%.4d_%.2d_%.2d %.2d_%.2d_%.2d]", lpr,
		(1900+p->tm_year), (1+p->tm_mon), p->tm_mday,
		p->tm_hour, p->tm_min, p->tm_sec);

	//save to txt
	const char lprFileName[] = "ivsresult.txt";
	FILE* file = fopen(lprFileName ,"at+");
	if (NULL == file)
	{
		perror("create txt file failed!");
		return ;
	}
	int size= strlen(imageFileName);
	if (fwrite(imageFileName, 1, size, file) == size)
	{
		//printf("write file ok!\n");
	}
	fputc('\n', file);
	fclose(file);
	//save image
	strcat(imageFileName,".jpg");
	file = fopen(imageFileName ,"wb");
	if (NULL == file)
	{
		perror("create image file failed!");
		return ;
	}
	int pos = strlen(recvBuffer) + 1;
	size= len - pos;
	if (fwrite(&recvBuffer[pos], 1, size, file) == size)
	{
		//printf("write file ok!\n");
	}
	printf("%s \n", imageFileName);
	fclose(file);
}
#endif


void parseBinIVSResult(char* cameraIp, char *pBuffer, unsigned len, char fileName[])
{
	BLOCK_HEADER* pHeader = (BLOCK_HEADER*)pBuffer;
	if (pHeader->length == 0)
	{
		//没有识别结果
	}
	else
	{
		//识别结果
		if (pHeader->type == BLOCK_TYPE_BIN_RESULT &&
			pHeader->length == sizeof(TH_PlateResult))
		{
			//二进制格式
			TH_PlateResult *pResult= (TH_PlateResult *)(&pBuffer[sizeof(BLOCK_HEADER)]);

		//	printf("licence:%s\n", pResult->license);
			LOGI("******************************************************");
			LOGI("uBitsTrigType:%d\n", pResult->uBitsTrigType);
			LOGI("licence:%s\n", pResult->license);
			LOGI("nColor:%d\n", pResult->nColor);
			LOGI("nType:%d\n", pResult->nType);
			LOGI("nConfidence:%d", pResult->nConfidence);
			LOGI("******************************************************");
			//LOGI("before get the picture");
			//判断有没有图片块头
		    int blockOffset = pHeader->length + sizeof(BLOCK_HEADER);
			if ( blockOffset + sizeof(BLOCK_HEADER) < len)
			{
				LOGI("***********is ture***************");
				BLOCK_HEADER* pImageHeader = (BLOCK_HEADER*)(&pBuffer[blockOffset]);
				blockOffset += sizeof(BLOCK_HEADER);
				//判断图片接收是否完整
				if (pImageHeader->type == BLOCK_TYPE_IMAGE_DATA &&
					blockOffset + pImageHeader->length <= len)
				{
					LOGI("***********pic is ture***************");
					LOGI("nConfidence:%d:%d", pResult->nConfidence,confidenceLevel);
					if (pResult->nConfidence >= confidenceLevel){
						LOGI("***********pic is ture2***************");
						if (pResult->uBitsTrigType == TRIGGER_TYPE_VLOOP_BIT)
						{
							LOGI("***********pic is ture3***************");
							g_pFunc(cameraIp, &pBuffer[blockOffset], pImageHeader->length, pResult->license, pResult->rcLocation.left, pResult->rcLocation.right, pResult->rcLocation.top, pResult->rcLocation.bottom, pResult->uBitsTrigType,
									pResult->nColor, pResult->nType);
						}
					}
					//补录来车
					LOGI("nConfidence:%d:%d:%d", pResult->uBitsTrigType,TRIGGER_TYPE_EXTERNAL_BIT,TRIGGER_TYPE_SOFTWARE_BIT);
					if (pResult->uBitsTrigType == TRIGGER_TYPE_EXTERNAL_BIT || pResult->uBitsTrigType == TRIGGER_TYPE_SOFTWARE_BIT)
					{
						LOGI("***********pic is ture4***************");
						g_pFunc(cameraIp, &pBuffer[blockOffset], pImageHeader->length, pResult->license, pResult->rcLocation.left, pResult->rcLocation.right, pResult->rcLocation.top, pResult->rcLocation.bottom, pResult->uBitsTrigType,
															pResult->nColor, pResult->nType);
					}
				}
			}
			//unsigned char *pImg, char *pRecoResult, int left, int top, int width, int height

		}		
		else
		{
			LOGI("***********is false***************");
			//其它格式
		}

	}
}
int doAsyncRecvImage(char* cameraIp, SOCKET client,int packetSize,char fileName[])
{
	char* recvBuffer = (char*)malloc(packetSize);
	int totleRecvSize = 0;
	int recvSize = 0;
	while(totleRecvSize < packetSize)
	{
		recvSize = recv(client, &recvBuffer[totleRecvSize],
			packetSize - totleRecvSize, 0);
		if (recvSize > 0)
		{
			totleRecvSize += recvSize;
		}
		else
		{
			printf("recv error code :%d\n", getLastError());
			free(recvBuffer);
			return 0;
		}						
	}

	if(totleRecvSize == packetSize)
	{
		//接收到完整的数据包后做格式化解析处理
#ifdef _USE_JSONCPP_LIB			
		saveForTest(recvBuffer, packetSize);
#else
		//接收的数据如果是二进制识别结果，会带有一个头BLOCK_HEADER
		if (totleRecvSize > sizeof(BLOCK_HEADER))
		{
			BLOCK_HEADER* pHeader = (BLOCK_HEADER*)recvBuffer;
			if (pHeader->magic[0] == 'I' && pHeader->magic[1] == 'R')
			{
				parseBinIVSResult(cameraIp, recvBuffer, totleRecvSize, fileName);
			}
			else
			{
				//其它json格式数据
				char *pJson = (char *)(&recvBuffer[0]);
				printf("ivs result:\n%s",pJson);
				LOGI("ivs result:\n%s",pJson);

				//判断有没有图片
				int ivsLen = strlen(pJson) + 1;
				if (ivsLen < totleRecvSize)
				{
					saveImage(&recvBuffer[ivsLen], totleRecvSize - ivsLen, fileName);
				}
			}
		}
#endif			
	}
	free(recvBuffer);
	return totleRecvSize;
}
/*void recvIVSResult(SOCKET s,char ivsResult[],int len, char fileName[])
{

	int packetSize = recvPacketSize(s);

	if (packetSize>0)
	{
		doAsyncRecvImage(s, packetSize,  fileName);
	}

}*/
//配置识别结果的传输格式及是否要接收图片
//fmt = 0 传输二进制结构体,fmt = 1 传输编码后的json字符串
//bImage，0为不传输图片，1为要传输图片
//bEnable, 0表示不主动推送，1表示主动推送
int cfgTransferFmt(SOCKET s, int bEnable, int fmt, int bImage)
{
	char ctl[256];
	char buff[256];
	sprintf(ctl, "{"
		"\"cmd\" : \"ivsresult\","
		"\"enable\" : %s,"
		"\"format\" : \"%s\","
		"\"image\" : %s"
		"}", 
		 bEnable?"true":"false",
		fmt==0?"bin":"json", 
		bImage?"true":"false");
	
	if ( sendCmd(s, ctl) == strlen(ctl)+1)
	{
		return 0;
	}
	else
	{
		return -1;
	}
}

int sendGetImg(SOCKET s)
{
	LOGI("sendGetImg+++++++++++++++++++++++=");
	if (sendCmd(s, triggerIVSResult) == strlen(triggerIVSResult) + 1)
	{
		return 0;
	}
	else
	{
		return -1;
	}
}

#define CMD_TRIGGER "trigger"
#define MAKE_JSON(cmd) "{cmd:\""##cmd"\"}"
int sendIOCtl(SOCKET s,int ioNum, int value)
{
	char json[256];
	int delay = 500;
	sprintf(json, "{"
		"\"cmd\" : \"ioctl\","
		"\"io\" : %d,"
		"\"value\" : %d,"
		"\"delay\" : %d"
		"}", 
		ioNum, value, delay);

	if ( sendCmd(s, json) == strlen(json)+1)
	{
		return 0;
	}
	else
	{
		return -1;
	}
}

void ctlPole(int cmd, char* ip)
{
	int count = 0;
	LOGI("controlPole ip = %s", ip);
	LOGI("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	LOGI("ip0 = %s, soket0= %d", cameraInfo[0].cameraIp, cameraInfo[0].socket);
	LOGI("ip0 = %s, soket0= %d", cameraInfo[1].cameraIp, cameraInfo[1].socket);
	LOGI("ip0 = %s, soket0= %d", cameraInfo[2].cameraIp, cameraInfo[2].socket);
	LOGI("ip0 = %s, soket0= %d", cameraInfo[3].cameraIp, cameraInfo[3].socket);
	LOGI("ip0 = %s, soket0= %d", cameraInfo[4].cameraIp, cameraInfo[4].socket);
	for (count = 0; count < 5 ; count++){
		LOGI("++++++++++++++++++++++++++++++++++");
		if (cameraInfo[count].cameraIp != NULL){
			LOGI("save ip[%d] = %s, socket[%d] = %d", count, cameraInfo[count].cameraIp,count, cameraInfo[count].socket);
			if (strcmp(ip, cameraInfo[count].cameraIp) == 0){
				LOGI("ip equals***************************************************");
				break;
			}
		}
	}
	LOGI("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	sendIOCtl(cameraInfo[count].socket,cmd, IO_OUT_SQUARE_HIGH);
}

int getOneImg(char *ip)
{
	int count = 0;
	for (count = 0; count < 5 ; count++){
			LOGI("++++++++++++++++++++++++++++++++++");
			if (cameraInfo[count].cameraIp != NULL){
				LOGI("save ip[%d] = %s, socket[%d] = %d", count, cameraInfo[count].cameraIp,count, cameraInfo[count].socket);
				if (strcmp(ip, cameraInfo[count].cameraIp) == 0){
					LOGI("ip equals***************************************************");
					break;
				}
			}
		}
	return sendGetImg(cameraInfo[count].socket);
}

int yitijiRun(char *cameraIp, ResultCallBack pRetCBFunc, OpenCameraCallBack pOpenCameraCBFunc, KeepAliveCallBack pKeepAliveCBFunc)
{
	int count = 0;
	SOCKET client;
	//WSADATA wsa;
	struct sockaddr_in addr;
	char buff[256];
	g_pFunc = pRetCBFunc;
	g_pKeepFunc = pKeepAliveCBFunc;
	unsigned long ip_addr = inet_addr(cameraIp);
	LOGI("ddddddddddddddddddddddddddddddddddddd");
	for (count = 0; count < 5; count++){
	LOGI("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");

		if (cameraInfo[count].cameraIp == NULL){
			LOGI("count = %d!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", count);
			break;
		}
	}

	LOGI("Step 111111111111");
	if (INADDR_NONE == ip_addr)
	{
		LOGI("INADDR_NONE ip");
		printf("input right ip,eg:192.168.1.22\n");
		return 0;
	}
	LOGI("Step 22222222");
	if(initializeWinsockIfNecessary() == 0)
	{
		printf("WSAStartup error!/n");
		LOGI("WSAStartup error!/n");

		return 0;
	}
	client=socket(AF_INET,SOCK_STREAM,IPPROTO_TCP);
	if(INVALID_SOCKET == client)
	{
		printf("create socket error/n");
		//releaseWinsockIfNecessary();
		LOGI("create socket error/n");
		return 0;
	}
	LOGI("Step 33333333333333333333333");

	makeSocketBlocking(client);

	addr.sin_family=AF_INET;
	addr.sin_port=htons(port);
	addr.sin_addr.s_addr= ip_addr;
	/* connect为阻塞试接口，当服务端关闭了，connect会阻塞将近75s左右  */
	if(connect(client,(struct sockaddr *)&addr,sizeof(addr))!=0)
	{
		printf("connect error/n");
		closeSocket(client);
		//releaseWinsockIfNecessary();
		LOGI("connect error/n");
		return 0;
	}
	LOGI("Step 444444444444444444444444444");
	//sendCmd(client,getsn);
	//recvPacket(client,buff, 256);
#ifdef _TEST_GET_IVS
	char ivsResult[1024];
	char fileName[_MAX_FNAME];
	sendCmd(client,getivsresult);
	//recvIVSResult(client,ivsResult, 1024,fileName);
#endif
	fd_set readSet;
	struct timeval tv = {0, 0}; 
	int ret;

	int fmt = 0; //0:二进制格式, 1:json格式
	if (cfgTransferFmt(client, 1, fmt, 1) < 0)
	{
		printf("cfg error!\n");
	}
	
	LOGI("ready to recv image!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
	
	sendKeepAlive(client);

	//定时发送心跳包，推荐使用系统的定时器功能,这里采用简单计时的方法
	long time_pre = time(NULL);
	long time_now = 0;
	pOpenCameraCBFunc();

	g_client = client;
	cameraInfo[count].cameraIp = cameraIp;
	cameraInfo[count].socket = client;

	LOGI("ip = %s, soket = %d", cameraInfo[count].cameraIp, cameraInfo[count].socket);
	while(1)
	{
		//wait for image upload
		tv.tv_sec = 15;
		FD_ZERO(&readSet);
		FD_SET(client, &readSet);
		
		ret = select(client+1,&readSet,NULL,NULL,&tv);
		printf("select ret:%d\n",ret);
		if(ret > 0)
		{			
			//LOGI("got the result");
			if (FD_ISSET(client, &readSet))
			{
				int packetSize = recvPacketSize(client);
				LOGI("recvPacketSize:%d\n",packetSize);
				printf("recvPacketSize:%d\n",packetSize);
				if (packetSize>0)
				{
					//接收数据处理，目前主要是识别结果的推送
					//可能包含其它结果，如获取到的序列号
					//具体内容根据接收到的json数据解析判断
					LOGI("packetSize>0");

					doAsyncRecvImage(cameraIp, client, packetSize, NULL);
				}
				else if (packetSize == 0)
				{
					//接收到心跳包的回应
					//LOGI("keep alive responsed\n");
					printf("keep alive responsed\n");
					g_pKeepFunc(keepalive);
				}
				else
				{
					//recv函数返回SOCKET_ERROR ，网络错误
					LOGI("in while recv error code :%d\n", getLastError());
					printf("recv error code :%d\n", getLastError());
					break;
				}
				
			}
		}
		else if (ret == 0)
		{
			//select timeout
		}
		else if (ret == SOCKET_ERROR)
		{
			printf("recv error code :%d\n",getLastError());
			break;
		}
		//需要定时发送心跳包
		time_now = time(NULL);
		if (time_now - time_pre > tv.tv_sec)
		{
			//timeout
			if (sendKeepAlive(client) != SOCKET_ERROR )
			{
				printf("sendKeepAlive\n");
				time_pre = time_now;
				continue;
			}
			else
			{
				// error 
				break;
			}			
		}



	}
	LOGI("yitiji exit============================");
	printf("recv error code :%d\n", getLastError());
	closeSocket(client);
	//releaseWinsockIfNecessary();
	//getchar();
	return -1;
}

int getConfidenceLevel()
{
	return confidenceLevel;
}

void setConfidenceLevel(int level)
{
	confidenceLevel = level;
	LOGI("confidenceLevel = %d", confidenceLevel);
}

void stop()
{
	LOGI("exit**************************************");
	exit(0);
}
