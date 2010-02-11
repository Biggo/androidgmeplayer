#ifndef GME_TYPES_H
#define GME_TYPES_H

/* CMake will either define the following to 1, or #undef it,
 * depending on the options passed to CMake.  This is used to
 * conditionally compile in the various emulator types.
 *
 * See gme_type_list() in gme.cpp
 */
#ifndef USE_GME_AY
#define USE_GME_AY
#endif
#ifndef USE_GME_GBS
#define USE_GME_GBS
#endif
#ifndef USE_GME_GYM
#define USE_GME_GYM
#endif
#ifndef USE_GME_HES
#define USE_GME_HES
#endif
#ifndef USE_GME_KSS
#define USE_GME_KSS
#endif
#ifndef USE_GME_NSF
#define USE_GME_NSF
#endif
#ifndef USE_GME_NSFE
#define USE_GME_NSFE
#endif
#ifndef USE_GME_SAP
#define USE_GME_SAP
#endif
#ifndef USE_GME_SPC
#define USE_GME_SPC
#endif
#ifndef USE_GME_VGM
#define USE_GME_VGM
#endif

#endif /* GME_TYPES_H */
