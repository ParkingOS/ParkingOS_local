package com.zld.lib.util;

import android.content.Context;
import android.util.Log;

import com.iflytek.speech.SynthesizerPlayer;
import com.zld.R;

public class VoicePlayer {
	private static VoicePlayer vp = null;
	private static SynthesizerPlayer player;
	
	private VoicePlayer(){}
	
	public static VoicePlayer getInstance(Context context){
		if (vp == null){
			vp = new VoicePlayer();
			player = SynthesizerPlayer.createSynthesizerPlayer(context, "appid="+context.getResources().getString(R.string.xunfei_id));
			player.setVoiceName("vixy");
			player.setVolume(100);
		}
		
		return vp;
	}
	
	public void playVoice(String content){
		player.playText(content, "tts_buffer_time=2000", null);
		Log.e("结算停车费", "播报语音");
	}
}
