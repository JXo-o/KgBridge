package edu.bjtu.kgbridge.builtins;

import org.apache.jena.graph.Node;
import org.apache.jena.reasoner.rulesys.RuleContext;
import org.apache.jena.reasoner.rulesys.builtins.BaseBuiltin;

/**
 * ClassName: NotEqual
 * Package: edu.bjtu.kgbridge.builtins
 * Description: ne原语，判断是否不相等
 *
 * @author JX
 * @version 1.0
 * @date 2024/5/19 14:34
 */
public class NotEqual extends BaseBuiltin {
    public NotEqual() {
    }

    @Override
    public String getName() {
        return "ne";
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
            return !str1.equals(str2);
        } else {
            return false;
        }
    }
}
