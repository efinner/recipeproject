package recipes.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import recipes.dao.RecipeDao;
import recipes.exception.DbException;

public class RecipeService {
	
private static final String SCHEMA_FILE ="recipe_schema.sql";
private static final String DATA_FILE = "recipe_data.sql";

private RecipeDao recipeDao = new RecipeDao();


public void createAndPopulateTables() {
	loadFromFile(SCHEMA_FILE);
	loadFromFile(DATA_FILE);
}

private void loadFromFile(String fileName) {
	String content = readFileContent(fileName);
	List<String> sqlStatements = convertContentToSqlStatments(content);
	
	
	
	recipeDao.excuteBatch(sqlStatements);
	
}

private List<String> convertContentToSqlStatments(String content) {
	content = removeComments(content);
	content = replaceWhiteSpaceSequecesWithSingleSpace(content);
	
	return extractLinesFromContent(content);
}

private List<String> extractLinesFromContent(String content) {
	List<String> lines = new LinkedList<>();
	while(!content.isEmpty()) {
		int semicolon = content.indexOf(";");
		if(semicolon ==-1) {
			if(!content.isBlank()) {
				lines.add(content);
			}
			content = "";
		}
		else {
			lines.add(content.substring(0,semicolon).trim());
			content = content.substring(semicolon +1);
		}
	}
	return lines;
	}

private String replaceWhiteSpaceSequecesWithSingleSpace(String content) {
	
	return content.replaceAll("\\s+", " ");
}

private String removeComments(String content) {
StringBuilder builder = new StringBuilder(content);
int commentPos = 0;

while((commentPos = builder.indexOf("-- ", commentPos))!=-1) {
	int eolPos = builder.indexOf("\n", commentPos + 1);
	
	if(eolPos ==-1) {
		builder.replace(commentPos, builder.length(), "");
	}
	else {
		builder.replace(commentPos, eolPos + 1, "");
	}
}



return builder.toString();
}

private String readFileContent(String fileName) {
	try {
		Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
		return Files.readString(path);
	} catch (Exception e) {
		throw new DbException(e);
	}
}


}
