package jsongooey.backend.parser;

import jsongooey.backend.jsonmodel.Value;

/**
 * key value pair in object
 * @param key
 * @param value
 */
public record Member(String key, Value value) {}
