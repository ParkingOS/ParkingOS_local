#ifndef IMG_TCPCLIENT_H_H
#define IMG_TCPCLIENT_H_H
typedef  void(*ResultCallBack)(unsigned char* cameraIp, unsigned char *pImg, int imgLength, char *pRecoResult, int left, int top, int width, int height, int resType, int carPlateColor, int nType);
typedef void(*FrameCallBack)(unsigned char *pImg, int width, int height);
typedef void(*OpenCameraCallBack)(void);
typedef void(*KeepAliveCallBack)(int keepalive);

int yitijiRun(char *cameraIp, ResultCallBack pRetCBFunc, OpenCameraCallBack pOpenCameraCBFunc, KeepAliveCallBack keepAliveRetCB);void stop();
void ctlPole(int cmd, char* cameraIp);
void setConfidenceLevel(int level);
int  getConfidenceLevel();
int  getKeepAlive();
#endif
