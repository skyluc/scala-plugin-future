package com.typesafe.luc.plugin.future.model;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.typesafe.luc.plugin.future.client.Client;

public class ThreeQueriesModelJava implements ModelJava {

	static final Sub[] FETCHING_SUBS = new Sub[] { new Sub("...", new Leaf[0]) };
	static final Leaf[] FETCHING_LEAVES = new Leaf[] { new Leaf("...") };

	private Root[] rootsCache = new Root[] { new Root("...", new Sub[0]) };
	private boolean fetched = false;
	private boolean fetching = false;

	private ArrayList<Runnable> refreshList = new ArrayList<Runnable>();

	@Override
	public Root[] rootsArray(Runnable refresh) {
		if (!fetched) {
			synchronized (refreshList) {
				refreshList.add(refresh);
				if (!fetching) {
					new FetchJob1().schedule();
					fetching = true;
				}
			}
		}
		return rootsCache;
	}

	private void refresh() {
		for (Runnable r : refreshList) {
			r.run();
		}
	}

	class FetchJob1 extends Job {

		public FetchJob1() {
			super("ThreeQueriesJava 1 job");
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// roots
			String[] rootIds = Client.getRootsArray();

			Root[] roots = new Root[rootIds.length];
			for (int i = 0; i < roots.length; i++) {
				roots[i] = new Root(rootIds[i], FETCHING_SUBS);
			}

			new FetchJob2(roots).schedule();

			rootsCache = roots;

			refresh();

			return Status.OK_STATUS;
		}
	}

	class FetchJob2 extends Job {

		private final Root[] roots;

		public FetchJob2(Root[] roots) {
			super("ThreeQueriesJava 2 job");
			this.roots = roots;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			// subs
			ArrayList<Sub> allSubs = new ArrayList<Sub>();

			for (Root root : roots) {
				String[] subIds = Client.getSubsArray(root.id());

				Sub[] subs = new Sub[subIds.length];
				for (int i = 0; i < subIds.length; i++) {
					Sub sub = new Sub(subIds[i], FETCHING_LEAVES);
					subs[i] = sub;
					allSubs.add(sub);
				}

				root.subs(subs);
			}

			new FetchJob3(allSubs).schedule();

			refresh();

			return Status.OK_STATUS;
		}
	}

	class FetchJob3 extends Job {

		private final ArrayList<Sub> subs;

		public FetchJob3(ArrayList<Sub> subs) {
			super("ThreeQueriesJava 3 job");
			this.subs = subs;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			for (Sub sub : subs) {
				String[] leafIds = Client.getLeavesArray(sub.id());

				Leaf[] leaves = new Leaf[leafIds.length];
				for (int i = 0; i < leafIds.length; i++) {
					leaves[i] = new Leaf(leafIds[i]);
				}
				sub.leaves(leaves);
			}

			synchronized (refreshList) {
				fetched = true;
			}

			refresh();

			return Status.OK_STATUS;
		}
	}
}
