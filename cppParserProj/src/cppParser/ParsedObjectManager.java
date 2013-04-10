package cppParser;

import java.util.ArrayList;
import java.util.Stack;

import cppParser.utils.Log;
import cppStructures.*;

/**
 * A singleton object manager for keeping record of
 * the CPP structures created in the parsing process.
 * 
 * @author Harri Pellikka
 */
public class ParsedObjectManager {

	// If 'true', the same class can be implemented in multiple ways (via PP directives)
	private boolean allowMultipleVariantsOfClass = true;
	
	private static ParsedObjectManager instance = new ParsedObjectManager();
	
	// Reference to the function currently under processing
	public CppFunc currentFunc = null;
	
	// Reference to the class or namespace currently under processing
	public CppScope currentScope = null;
	
	public String currentNameSpace = "";
	
	// List of scopes found
	private ArrayList<CppScope> scopes = new ArrayList<CppScope>();
	private Stack<CppScope> cppScopeStack = new Stack<CppScope>();
	
	private CppScope defaultScope = new CppScope("DEFAULT");
	
	// Array lists for misc. stuff found from source code
	ArrayList<String> oneLineComments = new ArrayList<String>();
	ArrayList<String> multiLineComments = new ArrayList<String>();
	ArrayList<String> defines = new ArrayList<String>();
	ArrayList<String> includes = new ArrayList<String>();
	ArrayList<String> classes = new ArrayList<String>();
    
    ArrayList<CppType> knownTypes=new ArrayList<>();
	
	/**
	 * Retrieves the singleton instance
	 * @return The singleton instance
	 */
	public static ParsedObjectManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Private constructor. Called only once by the class itself.
	 */
	private ParsedObjectManager()
	{
		
	}
    
    public ArrayList<CppType> getKnownTypes(){
        return knownTypes;
    }
    
    public void addKnownType(CppType type){
        for(CppType ct:knownTypes){
            if(ct.typeName.contentEquals(type.typeName))
                if(ct.parent.contentEquals(type.parent))
                    return;
        }
        knownTypes.add(type);
    }

	public void setDefaultScope()
	{
		currentScope = defaultScope;
	}
	
	public ArrayList<CppScope> getScopes()
	{
		return scopes;
	}
	
	public CppClass addClass(String name)
	{
		assert(name != null);
		
		CppClass newClass = null;
		for(CppScope cs : scopes)
		{
			if(cs instanceof CppClass)
			{
				if(cs.getName().equals(name))
				{
					Log.d("Found an existing scope " + name);
					newClass = (CppClass)cs;
					break;
				}
			}
		}
		
		if(newClass == null)
		{
			newClass = new CppClass(name);
			scopes.add(newClass);
            addKnownType(new CppType(name,CppType.CLASS));
		}
		
		return newClass;
	}

	public void addNamespace(CppNamespace ns, boolean addToStack) 
	{
		assert(ns != null);
		assert(ns.name != null);
		
		for(CppScope cs : scopes)
		{
			if(cs instanceof CppNamespace)
			{
				if(cs.getName().equals(ns.getName())) return;
			}
		}
		
		scopes.add(ns);
		if(addToStack) this.cppScopeStack.push(ns);
	}

	/**
	 * Stores the given function
	 * @param func Function to store
	 * @param b If 'true', set the new function as the current function
	 */
	public void addFunction(CppFunc func, boolean b)
	{
		func = currentScope.addFunc(func);
		if(b) currentFunc = func;
	}

	public Stack<CppScope> getCppScopeStack() {
		return this.cppScopeStack;
	}
}
