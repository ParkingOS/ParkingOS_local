package com.zld.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ContinueFTP {
	
	public enum UploadStatus {  
		 Create_Directory_Fail,      //远程服务器相应目录创建失败  
		 Create_Directory_Success,   //远程服务器闯将目录成功  
		 Upload_New_File_Success,    //上传新文件成功  
		 Upload_New_File_Failed,     //上传新文件失败  
		 File_Exits,                 //文件已经存在  
		 Remote_Bigger_Local,        //远程文件大于本地文件  
		 Upload_From_Break_Success,  //断点续传成功  
		 Upload_From_Break_Failed,   //断点续传失败  
		 Delete_Remote_Faild;        //删除远程文件失败  
	}  

	
	private FTPClient ftpClient = new FTPClient();
	
	public ContinueFTP(){
		//设置将过程中使用到的命令输出到控制台
		this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	}
	
	/**
	 * 连接到FTP服务器
	 * @param hostname 主机名
	 * @param port 端口
	 * @param username 用户名
	 * @param password 密码
	 * @return 是否连接成功
	 * @throws IOException
	 */
	public boolean connect(String hostname,int port,String username,String password) throws IOException{
		ftpClient.connect(hostname, port);
		if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
			if(ftpClient.login(username, password)){
				return true;
			}
		}
		disconnect();
		return false;
	}
	
	/**
	 * 从FTP服务器上下载文件
	 * @param remote 远程文件路径
	 * @param local 本地文件路径
	 * @return 是否成功
	 * @throws IOException
	 */
	public boolean download(String remote,String local) throws IOException{
		ftpClient.enterLocalPassiveMode();
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		boolean result;
		File f = new File(local);
		FTPFile[] files = ftpClient.listFiles(remote);
		if(files.length != 1){
			System.out.println("远程文件不唯一");
			return false;
		}
		long lRemoteSize = files[0].getSize();
		if(f.exists()){
			OutputStream out = new FileOutputStream(f,true);
			System.out.println("本地文件大小为:"+f.length());
			if(f.length() >= lRemoteSize){
				System.out.println("本地文件大小大于远程文件大小，下载中止");
				return false;
			}
			ftpClient.setRestartOffset(f.length());
			result = ftpClient.retrieveFile(remote, out);
			out.close();
		}else {
			OutputStream out = new FileOutputStream(f);
			result = ftpClient.retrieveFile(remote, out);
			out.close();
		}
		return result;
	}
	
	/**
	 * 上传文件到FTP服务器，支持断点续传
	 * @param local 本地文件名称，绝对路径
	 * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
	 * @return 上传结果
	 * @throws IOException
	 */
	public UploadStatus upload(String local,String remote) throws IOException{
		//设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		//设置以二进制流的方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		UploadStatus result;
		//对远程目录的处理
		String remoteFileName = remote;
		if(remote.contains("/")){
			remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
			String directory = remote.substring(0,remote.lastIndexOf("/")+1);
			if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(directory)){
				//如果远程目录不存在，则递归创建远程服务器目录
				int start=0;
				int end = 0;
				if(directory.startsWith("/")){
					start = 1;
				}else{
					start = 0;
				}
				end = directory.indexOf("/",start);
				while(true){
					String subDirectory = remote.substring(start,end);
					if(!ftpClient.changeWorkingDirectory(subDirectory)){
						if(ftpClient.makeDirectory(subDirectory)){
							ftpClient.changeWorkingDirectory(subDirectory);
						}else {
							System.out.println("创建目录失败");
							return UploadStatus.Create_Directory_Fail;
						}
					}
					
					start = end + 1;
					end = directory.indexOf("/",start);
					
					//检查所有目录是否创建完毕
					if(end <= start){
						break;
					}
				}
			}
		}
		
		//检查远程是否存在文件
		FTPFile[] files = ftpClient.listFiles(remoteFileName);
		if(files.length == 1){
			long remoteSize = files[0].getSize();
			File f = new File(local);
			long localSize = f.length();
			if(remoteSize==localSize){
				return UploadStatus.File_Exits;
			}else if(remoteSize > localSize){
				return UploadStatus.Remote_Bigger_Local;
			}
			
			//尝试移动文件内读取指针,实现断点续传
			InputStream is = new FileInputStream(f);
			if(is.skip(remoteSize)==remoteSize){
				ftpClient.setRestartOffset(remoteSize);
				if(ftpClient.storeFile(remote, is)){
					return UploadStatus.Upload_From_Break_Success;
				}
			}
			
			//如果断点续传没有成功，则删除服务器上文件，重新上传
			if(!ftpClient.deleteFile(remoteFileName)){
				return UploadStatus.Delete_Remote_Faild;
			}
			is = new FileInputStream(f);
			if(ftpClient.storeFile(remote, is)){	
				result = UploadStatus.Upload_New_File_Success;
			}else{
				result = UploadStatus.Upload_New_File_Failed;
			}
			is.close();
		}else {
			InputStream is = new FileInputStream(local);
			if(ftpClient.storeFile(remoteFileName, is)){
				result = UploadStatus.Upload_New_File_Success;
			}else{
				result = UploadStatus.Upload_New_File_Failed;
			}
			is.close();
		}
		return result;
	}
	/**
	 * 断开与远程服务器的连接
	 * @throws IOException
	 */
	public void disconnect() throws IOException{
		if(ftpClient.isConnected()){
			ftpClient.disconnect();
		}
	}
	
	public static void main(String[] args) {
		ContinueFTP myFtp = new ContinueFTP();
		try {
			myFtp.connect("118.192.91.210", 21, "tcbftp", "tqserver");
			System.out.println(myFtp.upload("c:\\err.txt", "/fff/aaas.txt"));
			myFtp.disconnect();
		} catch (IOException e) {
			System.out.println("连接FTP出错："+e.getMessage()); 
			e.printStackTrace();
		}
	}
}
