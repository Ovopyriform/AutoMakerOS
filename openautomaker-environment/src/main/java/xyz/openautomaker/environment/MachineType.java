package xyz.openautomaker.environment;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public enum MachineType
{
	//os.arch: x86_64 || aarch64
	//os.name: Mac OS X
	//

	//Linux
	// x86_64 || arm

	//Windows 

    /**
     *
     */
	WINDOWS("^Windows.*"),


    /**
	 * 
	 */
	LINUX("^Linux.*"),

    /**
     *
     */
	MAC("^Mac.*");

	String regex;

	private MachineType(String regex) {
		this.regex = regex;
	}

	String getRegex() {
		return this.regex;
	}
}
