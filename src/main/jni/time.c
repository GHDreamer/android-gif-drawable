#include "gif.h"

int calculateInvalidationDelay(GifInfo *info, time_t renderStartTime) {
    if (info->gifFilePtr->ImageCount > 1 && (info->currentLoop < info->loopCount || info->loopCount == 0)) {
        unsigned int scaledDuration = info->infos[info->currentIndex].duration;
        if (info->speedFactor != 1.0) {
            scaledDuration /= info->speedFactor;
            if (scaledDuration <= 0)
                scaledDuration = 1;
            else if (scaledDuration > INT_MAX)
                scaledDuration = INT_MAX;
        }
        info->nextStartTime = renderStartTime + scaledDuration;
        int invalidationDelay = (int) (scaledDuration - (getRealTime() - renderStartTime));
        if (invalidationDelay < 0)
            invalidationDelay = 0;
        return invalidationDelay;
    }
    info->lastFrameRemainder = 0;
    return -1;
}

inline time_t getRealTime() {
    struct timespec ts;
    if (clock_gettime(CLOCK_MONOTONIC_RAW, &ts) != -1)
        return ts.tv_sec * 1000 + ts.tv_nsec / 1000000;
    abort(); //should not happen since ts is in addressable space and CLOCK_MONOTONIC_RAW should be present
}