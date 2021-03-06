/*
 * Copyright © 2019. Sir Wellington.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("Checks")


package tech.sirwellington.alchemy.generator


/**
 * Used internally to perform argument checks.
 *
 * @author SirWellington
 */

@Throws(IllegalArgumentException::class)
@JvmOverloads
internal fun checkNotNull(ref: Any?, message: String = "")
{
    if (ref == null)
    {
        throw IllegalArgumentException(message)
    }
}

@Throws(IllegalArgumentException::class)
@JvmOverloads
internal fun checkThat(predicate: Boolean, message: String = "")
{
    if (!predicate)
    {
        throw IllegalArgumentException(message)
    }
}

@Throws(IllegalArgumentException::class)
@JvmOverloads
internal fun checkNotEmpty(string: String?, message: String? = "")
{
    checkThat(string != null && !string.isEmpty(), message ?: "")
}
