package jsongooey.jsonmodel;

public class StringValue implements Value {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        StringValue other = (StringValue) obj;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
