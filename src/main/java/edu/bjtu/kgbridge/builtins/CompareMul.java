package edu.bjtu.kgbridge.builtins;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * ClassName: CompareMul
 * Package: edu.bjtu.kgbridge.builtins
 * Description: compareMul通用原语
 * 举例：compareMul(?a, ge, 15. ?c)
 * 解释：判断?a是否大于等于15倍的?c
 * 注：第二个位置可为：ge、le、gt、lt
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 14:44
 */
public class CompareMul extends BaseBuiltin {
    public CompareMul() {
    }

    @Override
    public String getName() {
        return "compareMul";
    }

    @Override
    public int getArgLength() {
        return 4;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        Node x = getArg(0, args, context);
        Node a = getArg(1, args, context);
        Node y = getArg(2, args, context);
        Node z = getArg(3, args, context);

        if (x.isLiteral() && a.isLiteral() && y.isLiteral() && z.isLiteral()) {
            try {
                double xValue = Double.parseDouble(x.getLiteralLexicalForm());
                String operator = a.getLiteralLexicalForm();
                double yValue = Double.parseDouble(y.getLiteralLexicalForm());
                double zValue = Double.parseDouble(z.getLiteralLexicalForm());

                double result = yValue * zValue;

                return switch (operator) {
                    case "ge" -> xValue >= result;
                    case "le" -> xValue <= result;
                    case "gt" -> xValue > result;
                    case "lt" -> xValue < result;
                    case "eq" -> xValue == result;
                    case "ne" -> xValue != result;
                    default -> throw new IllegalArgumentException("Invalid operator: " + operator);
                };
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}