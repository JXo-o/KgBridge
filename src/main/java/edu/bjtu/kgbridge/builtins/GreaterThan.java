package edu.bjtu.kgbridge.builtins;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * ClassName: GreaterThan
 * Package: edu.bjtu.kgbridge.builtins
 * Description: gt原语，判断字符串大于
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 14:33
 */
public class GreaterThan extends BaseBuiltin {
    public GreaterThan() {
    }

    @Override
    public String getName() {
        return "gt";
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
            return str1.compareTo(str2) > 0;
        } else {
            return false;
        }
    }
}