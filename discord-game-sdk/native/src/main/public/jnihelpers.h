#ifndef JNIHELPERS_H
#define JNIHELPERS_H

#include <functional>
#include "jni.h"

namespace jnihelpers {
    void withEnv(JavaVM *jvm, const std::function<void(JNIEnv &)>& call);
} // namespace jnihelpers

#endif // JNIHELPERS_H
