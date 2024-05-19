package edu.bjtu.kgbridge.builtins;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * ClassName: LessThan
 * Package: edu.bjtu.kgbridge.builtins
 * Description: lt原语，判断字符串小于
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 14:32
 */
public class LessThan extends BaseBuiltin {
    public LessThan() {
    }

    @Override
    public String getName() {
        return "lt";
    }

    @Override
    public int getArgLength() {
        return 2;
    }

    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        Node n1 = this.getArg(0, args, context);
        Node n2 = this.getArg(1, args, context);
        if (n1.isLiteral() && n2.isLiteral()) {
            String str1 = n1.getLiteralLexicalForm();
            String str2 = n2.getLiteralLexicalForm();
            return str1.compareTo(str2) < 0;
        } else {
            return false;
        }
    }
}