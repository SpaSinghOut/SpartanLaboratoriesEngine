package com.spartanlaboratories.engine.util;

public abstract class Measurement<Element extends Measurement> {
	public abstract String toString();
	public abstract void duplicate(Element element);
	public abstract Element copy();
	public abstract void clear();
	public abstract void copyTo(Element element);
	public abstract boolean equals(Element element);
}
