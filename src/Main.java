public class Main {
    public static void main(String[] args) {
        Importer importer = new Importer();
        importer.go();

        System.out.println("\nImported Dependencies: " + importer.getImportedCount());
        System.out.println("\nMissed Dependencies: " + importer.getMissedCount());
        System.out.println("\nTotal: " + (importer.getImportedCount() + importer.getMissedCount()));
    }
}