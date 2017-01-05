# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

#jni中调用不能直接放lib文件夹下，需要在这里做配置
include $(CLEAR_VARS)
LOCAL_MODULE :=  H264Decoder-prebuilt
LOCAL_SRC_FILES := prebuilt/libH264Decoder.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  MediaConverter-prebuilt
LOCAL_SRC_FILES := prebuilt/libMediaConverter.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  MP4Recorder-prebuilt
LOCAL_SRC_FILES := prebuilt/libMP4Recorder.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  RTSP-prebuilt
LOCAL_SRC_FILES := prebuilt/libRTSP.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  RTSP_bak_2014-prebuilt
LOCAL_SRC_FILES := prebuilt/libRTSP_bak_2014.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  RTSP1-prebuilt
LOCAL_SRC_FILES := prebuilt/libRTSP1.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  tcpsdk-prebuilt
LOCAL_SRC_FILES := prebuilt/libtcpsdk.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE :=  vztcpsdk_dynamic-prebuilt
LOCAL_SRC_FILES := prebuilt/libvztcpsdk_dynamic.so
include $(PREBUILT_SHARED_LIBRARY)
#新加了8个so
include $(CLEAR_VARS)
LOCAL_MODULE :=  BaiduMapSDK_v3_1_0-prebuilt
LOCAL_SRC_FILES := prebuilt/libBaiduMapSDK_v3_1_0.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE :=  msc-prebuilt
LOCAL_SRC_FILES := prebuilt/libmsc.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := YITIJI

LOCAL_SRC_FILES := \
    com_zhenlaidian_photo_DecodeManager.h\
    yitiji.c\
    tcpclient.h\
    tcpclient.c\
   
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid     		
include $(BUILD_SHARED_LIBRARY)