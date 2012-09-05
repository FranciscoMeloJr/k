package ro.uaic.info.fmse.disambiguate;

import ro.uaic.info.fmse.k.ProductionItem.ProductionType;
import ro.uaic.info.fmse.k.Sort;
import ro.uaic.info.fmse.k.Term;
import ro.uaic.info.fmse.k.TermCons;
import ro.uaic.info.fmse.k.UserList;
import ro.uaic.info.fmse.loader.DefinitionHelper;

/**
 * Check to see which branch of an ambiguity has less type errors
 * 
 * @author RaduFmse
 * 
 */
public class GetFitnessUnitTypeCheckVisitor extends GetFitnessUnitBasicVisitor {

	@Override
	public void visit(TermCons tc) {
		super.visit(tc);

		if (tc.getProduction().getItems().get(0).getType() == ProductionType.USERLIST) {
			UserList ulist = (UserList) tc.getProduction().getItems().get(0);

			score += getFitnessUnit2(ulist.getSort(), tc.getContents().get(0).getSort());
			score += getFitnessUnit2(tc.getProduction().getSort(), tc.getContents().get(1).getSort());
		} else {
			int j = 0;
			for (int i = 0; i < tc.getProduction().getItems().size(); i++) {
				if (tc.getProduction().getItems().get(i).getType() == ProductionType.SORT) {
					Sort sort = (Sort) tc.getProduction().getItems().get(i);
					Term child = (Term) tc.getContents().get(j);
					score += getFitnessUnit2(sort.getName(), child.getSort());
					j++;
				}
			}
		}
	}

	/**
	 * Get the score for two sorts
	 * 
	 * @param declSort
	 *            - the sort declared in the production.
	 * @param termSort
	 *            - the sort found in the term.
	 * @return
	 */
	private int getFitnessUnit2(String declSort, String termSort) {
		if (termSort.equals(""))
			return 0; // if it is amb it won't have a sort
		int score;
		if (DefinitionHelper.isSubsortedEq(declSort, termSort))
			score = 0;
		// isSubsortEq(|"K", expect) ; (<?"K"> place <+ <?"K"> expect); !0
		else if (DefinitionHelper.isSubsortedEq("K", termSort) && (declSort.equals("K") || termSort.equals("K")))
			score = 0; // do nothing when you have a K
		else {
			score = -1;
			// System.out.println("Score: (" + declSort + "," + termSort + "," + score + ")");
		}
		return score;
	}

	@Override
	public GetFitnessUnitBasicVisitor getInstance() {
		return new GetFitnessUnitTypeCheckVisitor();
	}
}