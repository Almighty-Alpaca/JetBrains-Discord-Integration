/**
 * Copyright 2017-2020 Aljoscha Grebe
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef LOG_H
#define LOG_H

#include <iostream>

#define REACHED std::cout << __FILE__ << ":" << __LINE__ << std::endl;

#define REACHED_PRINT(ARG) std::cout << __FILE__ << ":" << __LINE__ << " - " << #ARG << "=" << ARG << std::endl;

#define REACHED_NOT_NULL(ARG) std::cout << __FILE__ << ":" << __LINE__ << " - " << #ARG << ((ARG) == nullptr ? "=null" : "!=null") << std::endl;

#define REACHED_POINTER(ARG) std::cout << __FILE__ << ":" << __LINE__ << " - " << #ARG << "=" << ((void*)(ARG)) << std::endl;

#define REACHED_EXCEPTION(ENV) std::cout << __FILE__ << ":" << __LINE__ << " - exception=" << ((ENV).ExceptionCheck() ? "true" : "false") << std::endl;

#endif // LOG_H
