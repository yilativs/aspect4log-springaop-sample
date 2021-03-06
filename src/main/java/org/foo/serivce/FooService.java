package org.foo.serivce;

import static net.sf.aspect4log.Log.Level.ERROR;
import static net.sf.aspect4log.Log.Level.INFO;
import static net.sf.aspect4log.Log.Level.TRACE;
import static net.sf.aspect4log.Log.Level.WARN;
import static net.sf.aspect4log.Log.Level.NONE;

import java.util.regex.Pattern;

import net.sf.aspect4log.Log;
import net.sf.aspect4log.Log.Exceptions;

import org.foo.dao.FooDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class FooService {
	@Autowired
	FooDao dao;

	public enum Gender {
		MALE, FEMALE
	}

	public String helloDefaultLogging(String name, Gender gender) {
		name = createGreeting(name, gender);
		dao.find(name);
		dao.saveOrUpdate(name);
		return name;

	}

	@Log(enterLevel = TRACE, exitLevel = INFO, on = @Exceptions(level = WARN))
	public String helloCustomizedLogLevel(String name, Gender gender) {
		return createGreeting(name, gender);
	}

	@Log(on = { @Exceptions(exceptions = IllegalArgumentException.class, level = WARN, stackTrace = false), @Exceptions(level = ERROR) })
	public String helloDifferentLogLevelForExceptions(String name, Gender gender) {
		return createGreeting(name, gender);
	}

	@Log(argumentsTemplate = "name=${args[0]}", resultTemplate = "${result}")
	public String helloCustomizedTemplate(String name, Gender gender) {
		return createGreeting(name, gender);
	}
	
	@Log(enterLevel=NONE,exitLevel=NONE)
	public void helloDoNotLog() {
		//do nothing
	}


	@Log(mdcKey = "userName", mdcTemplate = "${args[0]}")
	public String helloMdc(String name, Gender gender) {
		return createGreeting(name, gender);
	}

	@Log(mdcKey = "userName", mdcTemplate = "${args[0]}")
	public String helloIdent(String name, Gender gender) {
		return createGreeting(name, gender);
	}

	private String createGreeting(String name, Gender gender) {
		checkValidName(name);
		if (gender == Gender.MALE) {
			return "Hello Mr. " + name;
		} else {
			return "Hello Mrs. " + name;
		}
	}

	// starts with letter, has at least one space
	// (?=\w+ )^\w[\w ]*\w$
	private static final Pattern sillyNamePattern = Pattern.compile("(?=\\w+ )^\\w[\\w ]*\\w$", Pattern.CASE_INSENSITIVE);

	private void checkValidName(String name) {
		if (name == null) {
			throw new NullPointerException("name must not be null");
		}
		if (!sillyNamePattern.matcher(name).matches()) {
			throw new IllegalArgumentException(name + " - is not a propper name.");
		}
	}

}
