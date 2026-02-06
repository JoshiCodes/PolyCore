package de.joshicodes.polycore.util.packet;

public class AttackPayload {
    public String attacker;
    public int lines;

    public AttackPayload(String attacker, int lines) {
        this.attacker = attacker;
        this.lines = lines;
    }
}