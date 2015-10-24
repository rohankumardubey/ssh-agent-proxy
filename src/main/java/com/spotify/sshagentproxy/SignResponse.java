/**
 * Copyright (c) 2015 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotify.sshagentproxy;

import com.google.common.base.Objects;

/**
 * A class that represents SSH2_AGENT_SIGN_RESPONSE headers from the ssh-agent.
 */
class SignResponse extends AgentReply {

  // ssh-agent communication protocol constants
  static final int SSH2_AGENT_SIGN_RESPONSE = 14;

  private final int length;
  private final int responseCode;
  private final int responseLength;

  private SignResponse(final int length,
                       final int responseCode,
                       final int responseLength) {
    this.length = length;
    this.responseCode = responseCode;
    this.responseLength = responseLength;
  }

  static SignResponse from(final byte[] bytes) {
    if (bytes.length != 9) {
      throw new IllegalArgumentException("SSH2_AGENT_SIGN_RESPONSE eaders need to be 9 bytes");
    }

    // First four bytes represents length in bytes of rest of message
    final int length = first(bytes);

    // Next byte is the response code
    final int responseCode = second(bytes);
    if (responseCode != SSH2_AGENT_SIGN_RESPONSE) {
      throw new RuntimeException("Got the wrong response code for SSH2_AGENT_SIGN_RESPONSE.");
    }

    // Next four bytes is the response length
    final int responseLength = third(bytes);

    return new SignResponse(length, responseCode, responseLength);
  }

  int getLength() {
    return length;
  }

  int getResponseCode() {
    return responseCode;
  }

  int getResponseLength() {
    return responseLength;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SignResponse that = (SignResponse) o;

    if (length != that.length) {
      return false;
    }
    if (responseCode != that.responseCode) {
      return false;
    }
    return responseLength == that.responseLength;

  }

  @Override
  public int hashCode() {
    int result = length;
    result = 31 * result + responseCode;
    result = 31 * result + responseLength;
    return result;
  }
}
