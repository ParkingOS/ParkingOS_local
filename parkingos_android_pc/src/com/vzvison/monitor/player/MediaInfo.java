package com.vzvison.monitor.player;

import android.media.AudioFormat;

public class MediaInfo {
	private int width = 1000; // 鍥惧儚瀹藉害锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�
	private int height = 1000; // 鍥惧儚楂樺害锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�
	private int frameRate = 25; // 鍥惧儚甯х巼锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�

	private int reseve; // 淇濈暀锛屾亽涓�銆�
	private int channels = AudioFormat.CHANNEL_OUT_MONO;; // 澹伴亾鏁帮紝濡傛灉濯掍綋娴佷笉鍚湁闊抽锛屽垯涓�銆�
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;  // 閲囨牱娣卞害锛屽鏋滃獟浣撴祦涓嶅惈鏈夐煶棰戯紝鍒欎负0銆�
	private int sampleRate = 8000; // 閲囨牱鐜囷紝濡傛灉濯掍綋娴佷笉鍚湁闊抽锛屽垯涓�銆�
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public int getReseve() {
		return reseve;
	}

	public void setReseve(int reseve) {
		this.reseve = reseve;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public int getAudioFormat() {
		return audioFormat;
	}

	public void setAudioFormat(int audioFormat) {
		this.audioFormat = audioFormat;
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public void copy(MediaInfo other)
	{
		 width = other.width; // 鍥惧儚瀹藉害锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�
		height = other.height; // 鍥惧儚楂樺害锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�
		frameRate = other.frameRate; // 鍥惧儚甯х巼锛屽鏋滃獟浣撴祦涓嶅惈鏈夎棰戯紝鍒欎负0銆�

		reseve = other.reseve; // 淇濈暀锛屾亽涓�銆�
		channels = other.channels; // 澹伴亾鏁帮紝濡傛灉濯掍綋娴佷笉鍚湁闊抽锛屽垯涓�銆�
		audioFormat = other.audioFormat;  // 閲囨牱娣卞害锛屽鏋滃獟浣撴祦涓嶅惈鏈夐煶棰戯紝鍒欎负0銆�
		sampleRate = other.sampleRate; // 閲囨牱鐜囷紝濡傛灉濯掍綋娴佷笉鍚湁闊抽锛屽垯涓�銆�
	}
}
