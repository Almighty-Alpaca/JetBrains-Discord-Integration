
#include "com_example_greeter_Greeter.h"

#include <cstring>
#include <cstdlib>

#include "greeter.h"

static const char * ERROR_STRING = "name cannot be null";

JNIEXPORT jstring JNICALL Java_com_example_greeter_Greeter_sayHello(JNIEnv * env, jobject self, jstring name_from_java) {
	// Ensure parameter isn't null
	if (name_from_java == nullptr) {
		return env->NewStringUTF(ERROR_STRING);
	}

	// Convert jstring to std::string
	const char *name_as_c_str = env->GetStringUTFChars(name_from_java, nullptr);
	auto result_from_cpp = say_hello(name_as_c_str);  // Call native library
	env->ReleaseStringUTFChars(name_from_java, name_as_c_str);

	// Convert std::string to jstring
	auto result_buffer = static_cast<char*>(std::malloc(result_from_cpp.size()));
	std::strcpy(result_buffer, result_from_cpp.c_str());
	auto result = env->NewStringUTF(result_buffer);

	// Return result back to Java
	return result;
}
