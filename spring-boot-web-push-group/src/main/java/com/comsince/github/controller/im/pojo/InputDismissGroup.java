/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.controller.im.pojo;


import com.comsince.github.proto.FSCMessage;

public class InputDismissGroup extends InputGroupBase {
    private String group_id;


    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public boolean isValide() {
        return true;
    }

    public FSCMessage.DismissGroupRequest toProtoGroupRequest() {
        FSCMessage.DismissGroupRequest.Builder dismissGroupBuilder = FSCMessage.DismissGroupRequest.newBuilder();
        dismissGroupBuilder.setGroupId(group_id);

        if (to_lines != null) {
            for (Integer line : to_lines
            ) {
                dismissGroupBuilder.addToLine(line);
            }
        }

        if (notify_message != null) {
            dismissGroupBuilder.setNotifyContent(notify_message.toProtoMessageContent());
        }
        return dismissGroupBuilder.build();
    }

}
