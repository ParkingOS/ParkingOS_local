package com.vzvison.monitor.player;

import com.media.G711;
import com.media.H264Decoder;

@SuppressWarnings("unused")
public class Codec {
	public static final int CODEC_H264			= 1;
	public static final int CODEC_G711			= 2;
	public static final int CODEC_G726			= 3;
	public static final int CODEC_G729			= 4;
	
	private H264Decoder h264 = null;
	private G711 g711 = null;
	
	private int videoCodecType = CODEC_H264;
	private int audioCodecType = CODEC_G711;
	
	private int width;
	private int height;
	
	private int channel;
	private int audioEncoding;
	private int sampleRateInHz;
	
	private int handle = -1;
	
	public int setAudioCodecType(int type) {
		if(type != CODEC_G711 && type != CODEC_G726 && type != CODEC_G729) {
			return 0;
		} 
		audioCodecType = type;
		return 1;
	}
	
	public int setVideoCodecType(int type) {
		if(type != CODEC_H264) {
			return 0;
		} 
		videoCodecType = type;
		return 1;
	}
	
	public int initVideoDecoder(int width, int height) {
		this.width = width;
		this.height = height;
		
		int result = 0;
		switch (videoCodecType) {
		case CODEC_H264:
			if(null == h264) {
				h264 = H264Decoder.getInstance();//new H264Decoder();
			}
			
			handle = h264.add(1);
			//result = h264.init();
			break;
		default:
			break;
		}
		 
		 return handle;//result;
	}
	
	public int initAudioDecoder(int channel, int audioEncoding, int sampleRateInHz) {
		this.channel = channel;
		this.audioEncoding = audioEncoding;
		this.sampleRateInHz = sampleRateInHz;
		
		int result = 0;
		switch (audioCodecType) {
		case CODEC_G711:
			if(null == g711) {
				g711 = new G711();
			}
			result = 1;
			break;
		case CODEC_G726:
			break;
		case CODEC_G729:
			break;
		default:
			break;
		}
		return result;
	}
	
	public int initAudioEncoder(int channel, int audioEncoding, int sampleRateInHz) {
		this.channel = channel;
		this.audioEncoding = audioEncoding;
		this.sampleRateInHz = sampleRateInHz;
		
		int result = 0;
		switch (audioCodecType) {
		case CODEC_G711:
			result = 512;
			break;
		case CODEC_G726:
			break;
		case CODEC_G729:
			break;
		default:
			break;
		}
		
		return result;
	}
	
	public  int decodeVideo(byte[] src, int length, byte[] dst,int[] wah) {
		int result = 0;
		switch (videoCodecType) {
		case CODEC_H264:
			result = h264.decode(handle,src, length, dst,wah);
		default:
			break;
		}
		return result;
	}
	
	public int decodeAudio(byte[] src, int length, byte[] dst) {
		switch (audioCodecType) {
		case CODEC_G711:
			short[] temp = new short[dst.length];
			G711.ulaw2linear(src, temp, dst.length);
			System.arraycopy(temp, 0, dst, 0, dst.length);
			break;
		default:
			break;
		}
		return 0;
	}
	
	public int encodeAudio(byte[] src, int length, byte[] dst) {
		switch (audioCodecType) {
		case CODEC_G711:
			G711.linear2ulaw(src, length, dst);
			break;
		default:
			break;
		}
		return 0;
	}
	
	public void releaseVideoDecoder() {
		if(null != h264) {
			h264.release(handle);
		}
		
		h264 = null;
	}
	
	public void releaseAudioDecoder() {
	}
	
	public void releaseAudioEncoder() {
	}
}
