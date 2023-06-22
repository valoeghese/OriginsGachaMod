package valoeghese.originsgacha.test;

import valoeghese.originsgacha.util.Division;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDivision {
	public static void main(String[] args) {
		Division<Integer> division = new Division<>();
		Scanner scanner = new Scanner(System.in);

		final Pattern insert = Pattern.compile("insert (\\d+(\\.\\d+)?) (\\d+)");
		boolean noQuit;

		System.out.println("Starting Division<T> test...");

		do {
			System.out.print("\n> ");
			String command = scanner.nextLine();
			noQuit = !"quit".equals(command) && !"exit".equals(command);

			Matcher matcher = insert.matcher(command);

			if (matcher.matches()) {
				double loc = Double.parseDouble(matcher.group(1));
				int value = Integer.parseInt(matcher.group(3));
				division.addSection(loc, value);
			}
			else if ("print".equals(command)) {
				System.out.println(division);
			}
			else if (noQuit) {
				System.out.println("Unknown Command.");
			}
		} while (noQuit);

		System.out.println("Exiting Division<T> test...");
	}
}
