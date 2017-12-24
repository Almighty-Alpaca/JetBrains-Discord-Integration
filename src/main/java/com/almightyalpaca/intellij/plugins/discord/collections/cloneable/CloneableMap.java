package com.almightyalpaca.intellij.plugins.discord.collections.cloneable;

import java.util.Map;

public interface CloneableMap<K, V> extends Map<K, V>, ReallyCloneable<CloneableMap<K, V>>
{}
