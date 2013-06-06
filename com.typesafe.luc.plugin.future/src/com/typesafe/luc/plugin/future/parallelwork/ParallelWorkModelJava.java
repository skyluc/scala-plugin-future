package com.typesafe.luc.plugin.future.parallelwork;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ParallelWorkModelJava implements ParallelWorkModel {

	List<Element> internal = new ArrayList<Element>();
	private final ParallelWorkViewBase view;

	public ParallelWorkModelJava(ParallelWorkViewBase view) {
		this.view = view;
	}

	@Override
	public void addNew() {
		Element newElement = null;
		synchronized (this) {
			newElement = new Element(internal.size(), null);
			internal.add(newElement);
		}
		
		view.refresh();
		
		new FetchJob(newElement.id).schedule();
	}

	@Override
	public Object[] content() {
		return internal.toArray();
	}
	
	private void update(int idToUpdate, String value) {
		synchronized (this) {
			for(Element element: internal) {
				if (element.id == idToUpdate) {
					element.setValue(value);
				}
			}
		}
		view.refresh();
	}

	private static class Element {

		private final int id;
		private String value;

		public Element(int id, String value) {
			this.id = id;
			this.value = value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}

		public String toString() {
			if (value == null) {
				return id + " : ...";
			} else {
				return id + " : " + value;
			}
		}

	}
	
	private class FetchJob extends Job {

		private final int id;

		public FetchJob(int id) {
			super("Parallel work fetch job");
			this.id = id;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String value = Client.fetchValueWait(id);
			update(id, value);
			return Status.OK_STATUS;
		}
		
		
		
	}
}
