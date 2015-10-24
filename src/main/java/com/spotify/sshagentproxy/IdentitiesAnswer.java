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
 * A class that represents SSH2_AGENT_IDENTITIES_ANSWER headers from the ssh-agent.
 */
class IdentitiesAnswer extends AgentReply {

  // ssh-agent communication protocol constants
  static final int SSH2_AGENT_IDENTITIES_ANSWER = 12;

  private final int length;
  private final int responseCode;
  private final int count;

  private IdentitiesAnswer(final int length, final int responseCode, final int count) {
    this.length = length;
    this.responseCode = responseCode;
    this.count = count;
  }

  static IdentitiesAnswer from(final byte[] bytes) {
    if (bytes.length != 9) {
      throw new IllegalArgumentException("SSH2_AGENT_IDENTITIES_ANSWER headers need to be 9 bytes");
    }

    // First four bytes represents length in bytes of rest of message
    final int length = first(bytes);

    // Next byte is the response code
    final int responseCode = second(bytes);
    if (responseCode != SSH2_AGENT_IDENTITIES_ANSWER) {
      throw new RuntimeException("Got the wrong response code for SSH2_AGENT_IDENTITIES_ANSWER.");
    }

    // Next four bytes is the number of keys the agent has
    final int count = third(bytes);

    return new IdentitiesAnswer(length, responseCode, count);
  }

  int getLength() {
    return length;
  }

  int getResponseCode() {
    return responseCode;
  }

  int getCount() {
    return count;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("length", length)
        .add("responseCode", responseCode)
        .add("count", count)
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

    IdentitiesAnswer that = (IdentitiesAnswer) o;

    if (length != that.length) {
      return false;
    }
    if (responseCode != that.responseCode) {
      return false;
    }
    return count == that.count;

  }

  @Override
  public int hashCode() {
    int result = length;
    result = 31 * result + responseCode;
    result = 31 * result + count;
    return result;
  }
}
