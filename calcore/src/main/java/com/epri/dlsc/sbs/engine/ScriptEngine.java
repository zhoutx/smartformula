package com.epri.dlsc.sbs.engine;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.epri.dlsc.sbs.exception.CalEngineException;
import com.epri.dlsc.sbs.jsactuator.Js4DB;
import com.epri.dlsc.sbs.jsactuator.JsDATE;

/**
 * 脚本引擎
 */
final public class ScriptEngine {
	private static Compilable compilable;

	private ScriptEngine(){};
	
	public synchronized static Compilable getCompilable() {
		if (compilable == null) {
			javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
			Bindings jsActuators = new SimpleBindings();
			jsActuators.put("DB", new Js4DB());
			jsActuators.put("DATE", new JsDATE());
			engine.setBindings(jsActuators, ScriptContext.GLOBAL_SCOPE);
			compilable = (Compilable) engine;
		}
		return compilable;
	}

	public static String evel(CompiledScript JSFunction, Map<String, Object> map) {
		String ZORE = "0";
		String NAN = "Nan";//非数字
		String NULL = "null";//没有返回值
		String INFINITY = "Infinity";//除数为0
		
		try {
			Bindings bindings = new SimpleBindings(map);
			Object result = JSFunction.eval(bindings);
			if (result != null && result.toString().length() > 0) {
				if (result.toString().equals(NAN)
						|| result.toString().equals(NULL)
						|| result.toString().equals(INFINITY)) {
					return ZORE;
				}
				return result.toString();
			} else {
				return "void";
			}
		} catch (ScriptException e) {
			e.printStackTrace();
			throw new CalEngineException(e.toString());
		}
	}
}
