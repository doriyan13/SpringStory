package com.dori.SpringStory.utils;

import com.dori.SpringStory.logger.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public interface FormulaCalcUtils {
    Logger logger = new Logger(FormulaCalcUtils.class);
    // For calculation of expressions in the wz (mpCon, etc..)
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    static int calcValueFromFormula(String Formula, int x) {
        int result = 0;
        Formula = Formula.replace("\n", "").replace("\\n", "")
                .replace("\r", "").replace("\\r", "")
                .replace("u", "Math.ceil").replace("d", "Math.floor");
        // Verify what variable need to replace with skill lvl -
        String toReplace = Formula.contains("y") ? "y"
                : Formula.contains("X") ? "X"
                : "x";
        try {
            Object res = engine.eval(Formula.replace(toReplace, x + ""));
            if (res instanceof Double) {
                result = ((Double) res).intValue();
            } else if (res instanceof Integer) {
                result = (Integer) res;
            }
        } catch (Exception e) {
            logger.error("Error occurred while parsing the mpCon formula!");
            e.printStackTrace();
        }
        return result;
    }
}
