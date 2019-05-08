package migratePlan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.util.ResourceUtils;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.util.BambooServer;

/**
 * Plan configuration for Bamboo. Learn more on: <a href=
 * "https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class TestPlan {

	public static void main(String[] args) {

		final String START_PLAN = "new Plan(";
		final String END_PLAN = "return plan;";
		final String START_PERMISSION = "new PlanPermissions";
		final String END_PERMISSION = "return planPermission;";
		final String PLAN = "plan";
		final String PERMISSION = "permission";

		String bambooUrl = "http://200.14.165.86:18085";
		// By default credentials are read from the '.credentials' file.
		BambooServer bambooServer = new BambooServer(bambooUrl);

		String content = "";

		String newJavaClass = setHeader();

		// String que contendran los planes y permisos
		String addPlan = "List<Plan> plan = new ArrayList<Plan>();";
		String addPermission = "List<PlanPermissions> planPermission = new ArrayList<PlanPermissions>();";

		// Buscamos los archivos generados (*.java)
		List<String> listadoArchivos = getFiles();
		System.out.println("Cantidad de archivos: " + listadoArchivos.size());

		String filePath = "D:/Wokspace/BambooSpec/";

		for (String archivo : listadoArchivos) {

			String path = filePath + archivo;
			System.out.println("Path:" + path);
			try {
				content = new String(Files.readAllBytes(Paths.get(path)));
				// System.out.println(content);
			} catch (IOException e) {
				e.printStackTrace();
			}

			
			
			
			addPlan += setJavaCode(content, START_PLAN, END_PLAN, PLAN);
			addPermission += setJavaCode(content, START_PERMISSION, END_PERMISSION, PERMISSION);

		}

		newJavaClass += addPlan;
		newJavaClass += addPermission;
		newJavaClass += setFooter(bambooUrl);

		createFile(filePath, newJavaClass);

	}

	private static String getDate() {
		Calendar calendario = new GregorianCalendar();

		String date = String.valueOf(calendario.get(Calendar.DATE))
				.concat(String.valueOf(calendario.get(Calendar.MONTH)))
				.concat(String.valueOf(calendario.get(Calendar.YEAR)))
				.concat(String.valueOf(calendario.get(Calendar.HOUR)))
				.concat(String.valueOf(calendario.get(Calendar.MINUTE)))
				.concat(String.valueOf(calendario.get(Calendar.SECOND)));
		return date;
	}

	private static String setFooter(String bambooUrl) {

		return "\r\npublic static void main(String... argv) {\r\n"
				+ "        //By default credentials are read from the '.credentials' file.\r\n"
				+ "        BambooServer bambooServer = new BambooServer(\"" + bambooUrl + "\");\r\n"
				+ "for(Plan p : plan){\r\n" + "	bambooServer.publish(p);\r\n" + "}	\r\n" + "        \r\n"
				+ " for(PlanPermissions pp : planPermission){\r\n" + "	bambooServer.publish(pp);\r\n" + "}\r\n"
				+ "}\r\n}";

	}

	private static void createFile(String path, String data) {

		File file = new File(path.concat("exportBamboo").concat(getDate()).concat(".java"));

		try {
			// Create the file
			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

			// Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String setJavaCode(String content, String start, String end, String method) {
		String data = "";
		String setMethod = (method == "plan") ? "\nplan.add(" : "\nplanPermission.add(";

		String body = "";
		try {

			body = content.substring(content.indexOf(start), content.indexOf(end));
		} catch (Exception e) {
			System.out.println("Error al parsear");
			body = null;
		}

		if (body != null) {
			data += setMethod;
			data += body;
			data += ");";
		} else {
			data = "\n";
		}

		return data;
	}

	private static List<String> getFiles() {

		String path = "D:\\Wokspace\\BambooSpec";
		List<String> listado = new ArrayList<String>();

		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".java")) {
					listado.add(files);
				}
			}
		}
		return listado;
	}

	private static String setHeader() {
		return "import com.atlassian.bamboo.specs.api.BambooSpec;\n"
				+ "import java.util.ArrayList;\n"
				+ "import java.util.List;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.BambooKey;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.BambooOid;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.permission.Permissions;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.Job;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.Plan;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.Stage;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.configuration.AllOtherPluginsConfiguration;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.project.Project;\n"
				+ "import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;\n"
				+ "import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;\n"
				+ "import com.atlassian.bamboo.specs.builders.repository.git.UserPasswordAuthentication;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.BowerTask;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.CheckoutItem;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.GruntTask;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.NodeTask;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.NpmTask;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.ScriptTask;\n"
				+ "import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;\n"
				+ "import com.atlassian.bamboo.specs.model.task.ScriptTaskProperties;\n"
				+ "import com.atlassian.bamboo.specs.util.BambooServer;\n"
				+ "import com.atlassian.bamboo.specs.util.MapBuilder;\n\n" +

				"@BambooSpec\n" + "public class PlanSpec {\n";
	}

}
