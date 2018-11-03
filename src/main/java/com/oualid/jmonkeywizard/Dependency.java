package com.oualid.jmonkeywizard;

class Dependency {
    String name;
    String group;
    String version;
    String license;
    String classPath = "";
    Platform platform;

    enum Platform {
        ALL, DESKTOP, ANDROID, IOS, VR
    }

    Dependency(String name, String group, String version, String license, Platform platform) {
        this.name = name;
        this.group = group;
        this.version = version;
        this.license = license;
        this.platform = platform;
    }

    Dependency(String name, String group, String version, String license, Platform platform, String classPath) {
        this.name = name;
        this.group = group;
        this.version = version;
        this.license = license;
        this.platform = platform;
        this.classPath = classPath;
    }
}
