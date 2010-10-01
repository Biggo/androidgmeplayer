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

include $(CLEAR_VARS)

LOCAL_MODULE    := libgme
LOCAL_SRC_FILES += gme/Ay_Apu.cpp
LOCAL_SRC_FILES += gme/Ay_Apu.h
LOCAL_SRC_FILES += gme/Ay_Cpu.cpp
LOCAL_SRC_FILES += gme/Ay_Cpu.h
LOCAL_SRC_FILES += gme/Ay_Emu.cpp
LOCAL_SRC_FILES += gme/Ay_Emu.h
LOCAL_SRC_FILES += gme/blargg_common.h
LOCAL_SRC_FILES += gme/blargg_config.h
LOCAL_SRC_FILES += gme/blargg_endian.h
LOCAL_SRC_FILES += gme/blargg_source.h
LOCAL_SRC_FILES += gme/Blip_Buffer.cpp
LOCAL_SRC_FILES += gme/Blip_Buffer.h
LOCAL_SRC_FILES += gme/Classic_Emu.cpp
LOCAL_SRC_FILES += gme/Classic_Emu.h
LOCAL_SRC_FILES += gme/Data_Reader.cpp
LOCAL_SRC_FILES += gme/Data_Reader.h
LOCAL_SRC_FILES += gme/Dual_Resampler.cpp
LOCAL_SRC_FILES += gme/Dual_Resampler.h
LOCAL_SRC_FILES += gme/Effects_Buffer.cpp
LOCAL_SRC_FILES += gme/Effects_Buffer.h
LOCAL_SRC_FILES += gme/Fir_Resampler.cpp
LOCAL_SRC_FILES += gme/Fir_Resampler.h
LOCAL_SRC_FILES += gme/Gbs_Emu.cpp
LOCAL_SRC_FILES += gme/Gbs_Emu.h
LOCAL_SRC_FILES += gme/Gb_Apu.cpp
LOCAL_SRC_FILES += gme/Gb_Apu.h
LOCAL_SRC_FILES += gme/Gb_Cpu.cpp
LOCAL_SRC_FILES += gme/Gb_Cpu.h
LOCAL_SRC_FILES += gme/gb_cpu_io.h
LOCAL_SRC_FILES += gme/Gb_Oscs.cpp
LOCAL_SRC_FILES += gme/Gb_Oscs.h
LOCAL_SRC_FILES += gme/gme.cpp
LOCAL_SRC_FILES += gme/gme.h
LOCAL_SRC_FILES += gme/Gme_File.cpp
LOCAL_SRC_FILES += gme/Gme_File.h
LOCAL_SRC_FILES += gme/Gym_Emu.cpp
LOCAL_SRC_FILES += gme/Gym_Emu.h
LOCAL_SRC_FILES += gme/Hes_Apu.cpp
LOCAL_SRC_FILES += gme/Hes_Apu.h
LOCAL_SRC_FILES += gme/Hes_Cpu.cpp
LOCAL_SRC_FILES += gme/Hes_Cpu.h
LOCAL_SRC_FILES += gme/hes_cpu_io.h
LOCAL_SRC_FILES += gme/Hes_Emu.cpp
LOCAL_SRC_FILES += gme/Hes_Emu.h
LOCAL_SRC_FILES += gme/Kss_Cpu.cpp
LOCAL_SRC_FILES += gme/Kss_Cpu.h
LOCAL_SRC_FILES += gme/Kss_Emu.cpp
LOCAL_SRC_FILES += gme/Kss_Emu.h
LOCAL_SRC_FILES += gme/Kss_Scc_Apu.cpp
LOCAL_SRC_FILES += gme/Kss_Scc_Apu.h
LOCAL_SRC_FILES += gme/M3u_Playlist.cpp
LOCAL_SRC_FILES += gme/M3u_Playlist.h
LOCAL_SRC_FILES += gme/Multi_Buffer.cpp
LOCAL_SRC_FILES += gme/Multi_Buffer.h
LOCAL_SRC_FILES += gme/Music_Emu.cpp
LOCAL_SRC_FILES += gme/Music_Emu.h
LOCAL_SRC_FILES += gme/Nes_Apu.cpp
LOCAL_SRC_FILES += gme/Nes_Apu.h
LOCAL_SRC_FILES += gme/Nes_Cpu.cpp
LOCAL_SRC_FILES += gme/Nes_Cpu.h
LOCAL_SRC_FILES += gme/nes_cpu_io.h
LOCAL_SRC_FILES += gme/Nes_Fme7_Apu.cpp
LOCAL_SRC_FILES += gme/Nes_Fme7_Apu.h
LOCAL_SRC_FILES += gme/Nes_Namco_Apu.cpp
LOCAL_SRC_FILES += gme/Nes_Namco_Apu.h
LOCAL_SRC_FILES += gme/Nes_Oscs.cpp
LOCAL_SRC_FILES += gme/Nes_Oscs.h
LOCAL_SRC_FILES += gme/Nes_Vrc6_Apu.cpp
LOCAL_SRC_FILES += gme/Nes_Vrc6_Apu.h
LOCAL_SRC_FILES += gme/Nsfe_Emu.cpp
LOCAL_SRC_FILES += gme/Nsfe_Emu.h
LOCAL_SRC_FILES += gme/Nsf_Emu.cpp
LOCAL_SRC_FILES += gme/Nsf_Emu.h
LOCAL_SRC_FILES += gme/Sap_Apu.cpp
LOCAL_SRC_FILES += gme/Sap_Apu.h
LOCAL_SRC_FILES += gme/Sap_Cpu.cpp
LOCAL_SRC_FILES += gme/Sap_Cpu.h
LOCAL_SRC_FILES += gme/sap_cpu_io.h
LOCAL_SRC_FILES += gme/Sap_Emu.cpp
LOCAL_SRC_FILES += gme/Sap_Emu.h
LOCAL_SRC_FILES += gme/Sms_Apu.cpp
LOCAL_SRC_FILES += gme/Sms_Apu.h
LOCAL_SRC_FILES += gme/Sms_Oscs.h
LOCAL_SRC_FILES += gme/Snes_Spc.cpp
LOCAL_SRC_FILES += gme/Snes_Spc.h
LOCAL_SRC_FILES += gme/Spc_Cpu.cpp
LOCAL_SRC_FILES += gme/Spc_Cpu.h
LOCAL_SRC_FILES += gme/Spc_Dsp.cpp
LOCAL_SRC_FILES += gme/Spc_Dsp.h
LOCAL_SRC_FILES += gme/Spc_Emu.cpp
LOCAL_SRC_FILES += gme/Spc_Emu.h
LOCAL_SRC_FILES += gme/Vgm_Emu.cpp
LOCAL_SRC_FILES += gme/Vgm_Emu.h
LOCAL_SRC_FILES += gme/Vgm_Emu_Impl.cpp
LOCAL_SRC_FILES += gme/Vgm_Emu_Impl.h
LOCAL_SRC_FILES += gme/Ym2413_Emu.cpp
LOCAL_SRC_FILES += gme/Ym2413_Emu.h
LOCAL_SRC_FILES += gme/Ym2612_Emu.cpp
LOCAL_SRC_FILES += gme/Ym2612_Emu.h

LOCAL_LDLIBS := -lz

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := gme_player
LOCAL_SRC_FILES := GMEPlayerLib.cpp

LOCAL_STATIC_LIBRARIES := libgme

LOCAL_LDLIBS := -lz

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := gme_trackinfo
LOCAL_SRC_FILES := GMETrackInfo.cpp

LOCAL_STATIC_LIBRARIES := libgme

LOCAL_LDLIBS := -lz

include $(BUILD_SHARED_LIBRARY)