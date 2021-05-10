package com.flyn.mydb.Config;

public class ConditionalExpression {
    private boolean isLeftConstant=true;
    private String left=null;
    private String leftType=null;
    private String leftTableName=null;
    private String leftAttribute=null;
    private int leftcolumn;
    private boolean leftIsIndex=false;//左边索引列
    private IndexNode leftRoot=null;
    private boolean isRightConstant=true;
    private String right=null;
    private String rightType=null;
    private String rightTableName=null;
    private String rightAttribute=null;
    private int rightcolumn;
    private boolean rightIsIndex=false;//左边索引列
    private IndexNode rightRoot=null;
    private String oper;//条件比较符号

    public ConditionalExpression() {
        super();
    }
    public ConditionalExpression(ConditionalExpression c) {
        super();
        this.isLeftConstant = c.isLeftConstant;
        this.left = c.left;
        this.leftType = c.leftType;
        this.leftTableName = c.leftTableName;
        this.leftAttribute = c.leftAttribute;
        this.leftcolumn = c.leftcolumn;
        this.leftIsIndex = c.leftIsIndex;
        this.leftRoot = c.leftRoot;
        this.isRightConstant = c.isRightConstant;
        this.right = c.right;
        this.rightType = c.rightType;
        this.rightTableName = c.rightTableName;
        this.rightAttribute = c.rightAttribute;
        this.rightcolumn = c.rightcolumn;
        this.rightIsIndex = c.rightIsIndex;
        this.rightRoot = c.rightRoot;
        this.oper = c.oper;
    }

    public ConditionalExpression(boolean isLeftConstant, String left, String leftType, String leftTableName, String leftAttribute, int leftcolumn, boolean leftIsIndex, IndexNode leftRoot, boolean isRightConstant, String right, String rightType, String rightTableName, String rightAttribute, int rightcolumn, boolean rightIsIndex, IndexNode rightRoot, String oper) {
        this.isLeftConstant = isLeftConstant;
        this.left = left;
        this.leftType = leftType;
        this.leftTableName = leftTableName;
        this.leftAttribute = leftAttribute;
        this.leftcolumn = leftcolumn;
        this.leftIsIndex = leftIsIndex;
        this.leftRoot = leftRoot;
        this.isRightConstant = isRightConstant;
        this.right = right;
        this.rightType = rightType;
        this.rightTableName = rightTableName;
        this.rightAttribute = rightAttribute;
        this.rightcolumn = rightcolumn;
        this.rightIsIndex = rightIsIndex;
        this.rightRoot = rightRoot;
        this.oper = oper;
    }

    @Override
    public String toString() {
        return "ConditionalExpression{" +
                "isLeftConstant=" + isLeftConstant +
                ", left='" + left + '\'' +
                ", leftType='" + leftType + '\'' +
                ", leftTableName='" + leftTableName + '\'' +
                ", leftAttribute='" + leftAttribute + '\'' +
                ", leftcolumn=" + leftcolumn +
                ", leftIsIndex=" + leftIsIndex +
                ", leftRoot=" + leftRoot +
                ", isRightConstant=" + isRightConstant +
                ", right='" + right + '\'' +
                ", rightType='" + rightType + '\'' +
                ", rightTableName='" + rightTableName + '\'' +
                ", rightAttribute='" + rightAttribute + '\'' +
                ", rightcolumn=" + rightcolumn +
                ", rightIsIndex=" + rightIsIndex +
                ", rightRoot=" + rightRoot +
                ", oper='" + oper + '\'' +
                '}';
    }

    public boolean isLeftConstant() {
        return isLeftConstant;
    }

    public void setLeftConstant(boolean leftConstant) {
        isLeftConstant = leftConstant;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getLeftType() {
        return leftType;
    }

    public void setLeftType(String leftType) {
        this.leftType = leftType;
    }

    public String getLeftTableName() {
        return leftTableName;
    }

    public void setLeftTableName(String leftTableName) {
        this.leftTableName = leftTableName;
    }

    public String getLeftAttribute() {
        return leftAttribute;
    }

    public void setLeftAttribute(String leftAttribute) {
        this.leftAttribute = leftAttribute;
    }

    public int getLeftcolumn() {
        return leftcolumn;
    }

    public void setLeftcolumn(int leftcolumn) {
        this.leftcolumn = leftcolumn;
    }

    public boolean isLeftIsIndex() {
        return leftIsIndex;
    }

    public void setLeftIsIndex(boolean leftIsIndex) {
        this.leftIsIndex = leftIsIndex;
    }

    public IndexNode getLeftRoot() {
        return leftRoot;
    }

    public void setLeftRoot(IndexNode leftRoot) {
        this.leftRoot = leftRoot;
    }

    public boolean isRightConstant() {
        return isRightConstant;
    }

    public void setRightConstant(boolean rightConstant) {
        isRightConstant = rightConstant;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public String getRightTableName() {
        return rightTableName;
    }

    public void setRightTableName(String rightTableName) {
        this.rightTableName = rightTableName;
    }

    public String getRightAttribute() {
        return rightAttribute;
    }

    public void setRightAttribute(String rightAttribute) {
        this.rightAttribute = rightAttribute;
    }

    public int getRightcolumn() {
        return rightcolumn;
    }

    public void setRightcolumn(int rightcolumn) {
        this.rightcolumn = rightcolumn;
    }

    public boolean isRightIsIndex() {
        return rightIsIndex;
    }

    public void setRightIsIndex(boolean rightIsIndex) {
        this.rightIsIndex = rightIsIndex;
    }

    public IndexNode getRightRoot() {
        return rightRoot;
    }

    public void setRightRoot(IndexNode rightRoot) {
        this.rightRoot = rightRoot;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }
}
