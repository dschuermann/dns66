/* Copyright (C) 2016 Julian Andres Klode <jak@jak-linux.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.jak_linux.dns66;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class. This is serialized as JSON using read() and write() methods.
 *
 * @author Julian Andres Klode
 */
public class Configuration {
    private static final int VERSION = 1;
    public boolean autoStart = false;
    public Hosts hosts = new Hosts();
    public DnsServers dnsServers = new DnsServers();

    private static Hosts readHosts(JsonReader reader) throws IOException {
        Hosts hosts = new Hosts();
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "enabled":
                    hosts.enabled = reader.nextBoolean();
                    break;
                case "items":
                    hosts.items = readItemList(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return hosts;
    }

    private static DnsServers readDnsServers(JsonReader reader) throws IOException {
        DnsServers servers = new DnsServers();
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "enabled":
                    servers.enabled = reader.nextBoolean();
                    break;
                case "items":
                    servers.items = readItemList(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return servers;
    }

    private static List<Item> readItemList(JsonReader reader) throws IOException {
        reader.beginArray();
        List<Item> list = new ArrayList<>();
        while (reader.hasNext())
            list.add(readItem(reader));

        reader.endArray();
        return list;
    }

    private static Item readItem(JsonReader reader) throws IOException {
        Item item = new Item();
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "title":
                    item.title = reader.nextString();
                    break;
                case "location":
                    item.location = reader.nextString();
                    break;
                case "state":
                    item.state = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return item;
    }

    private static void writeHosts(JsonWriter writer, Hosts h) throws IOException {
        writer.beginObject();
        writer.name("enabled").value(h.enabled);
        writer.name("items");
        writeItemList(writer, h.items);
        writer.endObject();
    }

    private static void writeDnsServers(JsonWriter writer, DnsServers s) throws IOException {
        writer.beginObject();
        writer.name("enabled").value(s.enabled);
        writer.name("items");
        writeItemList(writer, s.items);
        writer.endObject();
    }

    private static void writeItemList(JsonWriter writer, List<Item> items) throws IOException {
        writer.beginArray();
        for (Item i : items) {
            writeItem(writer, i);
        }
        writer.endArray();
    }

    private static void writeItem(JsonWriter writer, Item i) throws IOException {
        writer.beginObject();
        writer.name("title").value(i.title);
        writer.name("location").value(i.location);
        writer.name("state").value(i.state);
        writer.endObject();
    }

    public void write(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("version").value(VERSION);
        writer.name("autoStart").value(autoStart);
        writer.name("hosts");
        writeHosts(writer, hosts);
        writer.name("dnsServers");
        writeDnsServers(writer, dnsServers);
        writer.endObject();
    }

    public void read(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            switch (reader.nextName()) {
                case "version":
                    if (reader.nextInt() > VERSION) {
                        throw new RuntimeException("Incompatible format");
                    }
                    break;
                case "autoStart":
                    autoStart = reader.nextBoolean();
                    break;
                case "hosts":
                    hosts = readHosts(reader);
                    break;
                case "dnsServers":
                    dnsServers = readDnsServers(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
    }

    public static class Item {
        public static final int STATE_IGNORE = 2;
        public static final int STATE_DENY = 0;
        public static final int STATE_ALLOW = 1;
        public String title = "";
        public String location = "";
        public int state = STATE_IGNORE;
    }

    public static class Hosts {
        public boolean enabled = false;
        public List<Item> items = new ArrayList<>();
    }

    public static class DnsServers {
        public boolean enabled = false;
        public List<Item> items = new ArrayList<>();
    }
}
