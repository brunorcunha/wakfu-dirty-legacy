package com.ankamagames.framework.ai.criteria.antlrcriteria;

public class OrCriterion extends BooleanOperators
{
    private SimpleCriterion m1;
    private SimpleCriterion m2;
    
    @Override
    public int getValidity(final Object criterionUser, final Object criterionTarget, final Object criterionContent, final Object criterionContext) {
        if (this.m1.isValid(criterionUser, criterionTarget, criterionContent, criterionContext) || this.m2.isValid(criterionUser, criterionTarget, criterionContent, criterionContext)) {
            return 0;
        }
        return -1;
    }
    
    private OrCriterion(final SimpleCriterion left, final SimpleCriterion right) {
        super();
        this.m1 = left;
        this.m2 = right;
    }
    
    public SimpleCriterion getLeft() {
        return this.m1;
    }
    
    public SimpleCriterion getRight() {
        return this.m2;
    }
    
    @Override
    public boolean isConstant() {
        return false;
    }
    
    public static SimpleCriterion generate(final ParserObject pleft, final ParserObject pright) {
        BooleanOperators.checkType(pleft, pright);
        final SimpleCriterion left = (SimpleCriterion)pleft;
        final SimpleCriterion right = (SimpleCriterion)pright;
        if (left.isConstant()) {
            if (left.isValid(null, null, null, null)) {
                return new ConstantBooleanCriterion(true);
            }
            return right;
        }
        else {
            if (!right.isConstant()) {
                return new OrCriterion(left, right);
            }
            if (right.isValid(null, null, null, null)) {
                return new ConstantBooleanCriterion(true);
            }
            return left;
        }
    }
    
    @Override
    public Enum getEnum() {
        return CriterionIds.OR;
    }
}
