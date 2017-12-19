/**
 * Copyright Google Inc. All Rights Reserved.
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
package com.google.firebase.codelab.friendlychat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class FriendlyMessage {

    private String from;
    private String msg;
    private Date time;

    public FriendlyMessage() {}

    public FriendlyMessage(String from, String msg, Date time) {
        this.from = from;
        this.msg = msg;
        this.time = time;
    }

    public String getFrom() { return from; }

    public String getMsg() { return msg; }

    @ServerTimestamp
    public Date getTime() { return time; }
}
