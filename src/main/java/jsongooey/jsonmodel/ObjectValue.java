package jsongooey.jsonmodel;

import jsongooey.parser.Member;

import java.util.HashMap;
import java.util.Map;

public class ObjectValue implements Value {
    private Map<String, Value> members = new HashMap<>();

    public Map<String, Value> getMembers() {
        return members;
    }

    public void addMember(String name, Value value) {
        members.put(name, value);
    }

    public void addMember(Member member) {
        members.put(member.key(), member.value());
    }
}
