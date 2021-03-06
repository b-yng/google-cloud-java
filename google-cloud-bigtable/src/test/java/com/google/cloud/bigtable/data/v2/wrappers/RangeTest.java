/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigtable.data.v2.wrappers;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.bigtable.data.v2.wrappers.Range.BoundType;
import com.google.cloud.bigtable.data.v2.wrappers.Range.ByteStringRange;
import com.google.cloud.bigtable.data.v2.wrappers.Range.TimestampRange;
import com.google.protobuf.ByteString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RangeTest {
  @Test
  public void timestampUnboundedTest() {
    TimestampRange range = TimestampRange.unbounded();
    assertThat(range.getStartBound()).isEqualTo(BoundType.UNBOUNDED);
    assertThat(range.getEndBound()).isEqualTo(BoundType.UNBOUNDED);

    Throwable actualError = null;
    try {
      //noinspection ResultOfMethodCallIgnored
      range.getStart();
    } catch (Throwable e) {
      actualError = e;
    }
    assertThat(actualError).isInstanceOf(IllegalStateException.class);

    try {
      //noinspection ResultOfMethodCallIgnored
      range.getEnd();
    } catch (Throwable e) {
      actualError = e;
    }
    assertThat(actualError).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void timestampOfTest() {
    TimestampRange range = TimestampRange.create(10, 2_000);
    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(10);
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(2_000);
  }

  @Test
  public void timestampChangeStartTest() {
    TimestampRange range = TimestampRange.create(10, 2_000).startOpen(20L);

    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(2_000);

    assertThat(range.getStartBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getStart()).isEqualTo(20);

    range = range.startClosed(30L);
    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(30);
  }

  @Test
  public void timestampChangeEndTest() {
    TimestampRange range = TimestampRange.create(10, 2_000).endClosed(1_000L);

    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(10);

    assertThat(range.getEndBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getEnd()).isEqualTo(1_000);

    range = range.endOpen(3_000L);
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(3_000);
  }

  @Test
  public void byteStringUnboundedTest() {
    ByteStringRange range = ByteStringRange.unbounded();
    assertThat(range.getStartBound()).isEqualTo(BoundType.UNBOUNDED);
    assertThat(range.getEndBound()).isEqualTo(BoundType.UNBOUNDED);

    Throwable actualError = null;
    try {
      range.getStart();
    } catch (Throwable e) {
      actualError = e;
    }
    assertThat(actualError).isInstanceOf(IllegalStateException.class);

    try {
      range.getEnd();
    } catch (Throwable e) {
      actualError = e;
    }
    assertThat(actualError).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void byteStringOfTest() {
    ByteStringRange range =
        ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("b"));

    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("b"));
  }

  @Test
  public void byteStringOfStringTest() {
    ByteStringRange range = ByteStringRange.create("a", "b");

    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("b"));
  }

  @Test
  public void byteStringChangeStartTest() {
    ByteStringRange range =
        ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("z"))
            .startOpen(ByteString.copyFromUtf8("b"));

    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("z"));

    assertThat(range.getStartBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("b"));

    range = range.startClosed(ByteString.copyFromUtf8("c"));
    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("c"));
  }

  @Test
  public void byteStringChangeStartStringTest() {
    ByteStringRange range = ByteStringRange.create("a", "z").startOpen("b");

    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("z"));

    assertThat(range.getStartBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("b"));

    range = range.startClosed("c");
    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("c"));
  }

  @Test
  public void byteStringChangeEndTest() {
    ByteStringRange range =
        ByteStringRange.create(ByteString.copyFromUtf8("a"), ByteString.copyFromUtf8("z"))
            .endClosed(ByteString.copyFromUtf8("y"));

    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));

    assertThat(range.getEndBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("y"));

    range = range.endOpen(ByteString.copyFromUtf8("x"));
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("x"));
  }

  @Test
  public void byteStringChangeEndStringTest() {
    ByteStringRange range = ByteStringRange.create("a", "z").endClosed("y");

    assertThat(range.getStartBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getStart()).isEqualTo(ByteString.copyFromUtf8("a"));

    assertThat(range.getEndBound()).isEqualTo(BoundType.CLOSED);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("y"));

    range = range.endOpen("x");
    assertThat(range.getEndBound()).isEqualTo(BoundType.OPEN);
    assertThat(range.getEnd()).isEqualTo(ByteString.copyFromUtf8("x"));
  }
}
