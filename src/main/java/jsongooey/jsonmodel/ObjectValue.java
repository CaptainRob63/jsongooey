package jsongooey.jsonmodel;

import jsongooey.parser.Member;

import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectValue implements Value {
    // LinkedHashMap to preserve input order of members
    private Map<String, Value> members = new LinkedHashMap<>();

    public Map<String, Value> getMembers() {
        return members;
    }

    public ObjectValue addMember(String name, Value value) {
        members.put(name, value);
        return this;
    }

    public ObjectValue addMember(String name, String value) {
        members.put(name, new StringValue(value));
        return this;
    }

    public ObjectValue addMember(String name, double value) {
        members.put(name, new NumberValue(value));
        return this;
    }

    public ObjectValue addMember(String name, boolean value) {
        members.put(name, new BooleanValue(value));
        return this;
    }

    public ObjectValue addMember(Member member) {
        members.put(member.key(), member.value());
        return this;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();

        out.append("{");
        for (var entry : members.entrySet()) {
            out
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(", ");
        }
        if (out.length() > 2) out.deleteCharAt(out.length() - 2);
        out.append("}");

        return out.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ObjectValue other = (ObjectValue) obj;
        return members.equals(other.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }
}
