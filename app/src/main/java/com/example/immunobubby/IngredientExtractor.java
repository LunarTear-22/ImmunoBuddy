package com.example.immunobubby;

import java.util.*;
import java.util.regex.*;

public final class IngredientExtractor {

    // 🔑 Parole chiave in più lingue (Ingredienti / Composizione)
    static final Map<String, String[]> KEYS_BY_LANG = new HashMap<String, String[]>() {{
        put("I", new String[]{"ingredienti", "composizione"});
        put("GB", new String[]{"ingredients", "composition"});
        put("F", new String[]{"ingrédients", "composition"});
        put("E", new String[]{"ingredientes", "composición"});
        put("D", new String[]{"zutaten", "zusammensetzung"});
    }};

    private static final Map<String, String[]> ALLERGENS_BY_LANG = new HashMap<String, String[]>() {{
        // Italiano
        put("I", new String[]{
                // Alimentari
                "glutine","latte","uova","arachidi","soia","frutta a guscio","mandorle","nocciole","noci","pistacchi",
                "pesce","crostacei","granchio","gamberi","sedano","carote","cipolle","senape","sesamo","lupini","molluschi","solfiti",
                "frutta","agrumi","fragole","kiwi","pomodoro","peperoni","mais","riso","farro","orzo","avena",
                // Farmaci
                "penicillina","amoxicillina","aspirina","ibuprofene","paracetamolo","codeina","sulfamidici","cortisone","insulina","vaccini","antibiotici",
                "antistaminici","antifungini","chemioterapici",
                // Chimici / cosmetici / pelle
                "nichel","cobalto","cromo","parabeni","siliconi","profumi","coloranti","conservanti","resine","latex","polisorbati",
                "sodio laureth sulfate","formaldeide","benzofenone","retinolo","toluene","fenossietanolo","triclosan","cloruro di benzalconio",
                "acetato di etile","acetone","alcool benzilico","olio essenziale","coccoamidopropil betaina","urea","benzil benzoato",
                // Ambientali / inalanti / materiali
                "polline","acari della polvere","muffe","spore","piume","animali domestici","pelliccia","gatti","cani","conigli",
                "peli di cavallo","legno","pollini di graminacee","polvere di casa",
                // Altri chimici
                "glutaraldeide","acido citrico","ammoniaca","bromo","cloro","fluoro","zolfo","sodio","potassio","rame","ferro",
                "resine epossidiche","vernici","detergenti","candeggina","solventi","pesticidi","fertilizzanti","insetticidi"
        });

        // Inglese (GB)
        put("GB", new String[]{
                "gluten","milk","eggs","peanuts","soy","tree nuts","almonds","hazelnuts","walnuts","pistachios",
                "fish","crustaceans","crab","shrimp","celery","carrots","onions","mustard","sesame","lupin","molluscs","sulphites",
                "fruit","citrus","strawberries","kiwi","tomato","peppers","corn","rice","spelt","barley","oats",
                "penicillin","amoxicillin","aspirin","ibuprofen","paracetamol","codeine","sulfonamides","cortisone","insulin","vaccines","antibiotics",
                "antihistamines","antifungals","chemotherapy",
                "nickel","cobalt","chromium","parabens","silicones","fragrances","colorants","preservatives","resins","latex","polysorbates",
                "sodium laureth sulfate","formaldehyde","benzophenone","retinol","toluene","phenoxyethanol","triclosan","benzalkonium chloride",
                "ethyl acetate","acetone","benzyl alcohol","essential oil","cocamidopropyl betaine","urea","benzyl benzoate",
                "pollen","dust mites","molds","spores","feathers","pets","fur","cats","dogs","rabbits",
                "horse hair","wood","grass pollen","house dust",
                "glutaraldehyde","citric acid","ammonia","bromine","chlorine","fluorine","sulfur","sodium","potassium","copper","iron",
                "epoxy resins","paints","detergents","bleach","solvents","pesticides","fertilizers","insecticides"
        });

        // Francese
        put("F", new String[]{
                "gluten","lait","œufs","arachides","soja","fruits à coque","amandes","noisettes","noix","pistaches",
                "poisson","crustacés","crabe","crevettes","céleri","carottes","oignons","moutarde","sésame","lupin","mollusques","sulfites",
                "fruits","agrumes","fraises","kiwi","tomate","poivrons","maïs","riz","épeautre","orge","avoine",
                "pénicilline","amoxicilline","aspirine","ibuprofène","paracétamol","codéine","sulfamides","cortisone","insuline","vaccins","antibiotiques",
                "antihistaminiques","antifongiques","chimiothérapie",
                "nickel","cobalt","chrome","parabènes","silicones","parfums","colorants","conservateurs","résines","latex","polysorbates",
                "sodium laureth sulfate","formaldéhyde","benzophénone","rétinol","toluène","phénoxyéthanol","triclosan","chlorure de benzalkonium",
                "acétate d’éthyle","acétone","alcool benzylique","huile essentielle","cocamidopropyl bétaïne","urée","benzyl benzoate",
                "pollen","acariens","moisissures","spores","plumes","animaux domestiques","fourrure","chats","chiens","lapins",
                "poils de cheval","bois","pollens de graminées","poussière domestique",
                "glutaraldéhyde","acide citrique","ammoniac","brome","chlore","fluor","soufre","sodium","potassium","cuivre","fer",
                "résines époxy","peintures","détergents","eau de javel","solvants","pesticides","engrais","insecticides"
        });

        // Spagnolo
        put("E", new String[]{
                "gluten","leche","huevos","cacahuetes","soja","frutos secos","almendras","avellanas","nueces","pistachos",
                "pescado","crustáceos","cangrejo","camarones","apio","zanahorias","cebollas","mostaza","sésamo","altramuces","moluscos","sulfitos",
                "frutas","cítricos","fresas","kiwi","tomate","pimientos","maíz","arroz","espelta","cebada","avena",
                "penicilina","amoxicilina","aspirina","ibuprofeno","paracetamol","codeína","sulfonamidas","cortisona","insulina","vacunas","antibióticos",
                "antihistamínicos","antifúngicos","quimioterapia",
                "níquel","cobalto","cromo","parabenos","siliconas","fragancias","colorantes","conservantes","resinas","látex","polisorbatos",
                "lauril sulfato de sodio","formaldehído","benzofenona","retinol","tolueno","fenoxietanol","triclosán","cloruro de benzalconio",
                "acetato de etilo","acetona","alcohol bencílico","aceite esencial","betaína de cocamidopropilo","urea","benzoato de bencilo",
                "polen","ácaros del polvo","moho","esporas","plumas","mascotas","pelaje","gatos","perros","conejos",
                "pelo de caballo","madera","polen de gramíneas","polvo doméstico",
                "glutaraldehído","ácido cítrico","amoníaco","bromo","cloro","flúor","azufre","sodio","potasio","cobre","hierro",
                "resinas epoxi","pinturas","detergentes","lejía","solventes","pesticidas","fertilizantes","insecticidas"
        });

        // Tedesco
        put("D", new String[]{
                "gluten","milch","eier","erdnüsse","soja","schalenfrüchte","mandeln","haselnüsse","nüsse","pistazien",
                "fisch","krebstiere","krabben","garnelen","sellerie","karotten","zwiebeln","senf","sesam","lupinen","weichtiere","sulphite",
                "obst","zitrusfrüchte","erdbeeren","kiwi","tomate","paprika","mais","reis","dinkel","gerste","hafer",
                "penicillin","amoxicillin","aspirin","ibuprofen","paracetamol","codein","sulfonamide","kortison","insulin","impfstoffe","antibiotika",
                "antihistaminika","antimykotika","chemotherapie",
                "nickel","kobalt","chrom","parabene","silicone","duftstoffe","farbstoffe","konservierungsmittel","harze","latex","polysorbate",
                "natrium laureth sulfate","formaldehyd","benzophenon","retinol","toluol","phenoxyethanol","triclosan","benzalkoniumchlorid",
                "ethylacetat","aceton","benzylalkohol","ätherisches öl","cocamidopropyl betain","harnstoff","benzylbenzoat",
                "pollen","hausstaubmilben","schimmel","sporen","federn","haustiere","pelz","katzen","hunde","kaninchen",
                "pferdehaare","holz","gras pollen","hausstaub",
                "glutaraldehyd","zitronensäure","ammoniak","brom","chlor","fluor","schwefel","natrium","kalium","kupfer","eisen",
                "epoxidharze","farben","reinigungsmittel","bleiche","lösungsmittel","pestizide","dünger","insektizide"
        });
    }};


    public static String extract(String ocrText) {
        if (ocrText == null || ocrText.isEmpty()) return null;

        // Tronca dopo "non contiene"
        Pattern nonContienePattern = Pattern.compile("non\\s+contiene\\s*:?", Pattern.CASE_INSENSITIVE);
        Matcher m = nonContienePattern.matcher(ocrText);
        String textToScan = ocrText;
        if (m.find()) {
            textToScan = ocrText.substring(0, m.start());
        }

        // Converti tutto in minuscolo
        String textLower = textToScan.toLowerCase();

        // Cerca le parole chiave ingredienti/composizione
        for (Map.Entry<String, String[]> entry : KEYS_BY_LANG.entrySet()) {
            for (String key : entry.getValue()) {
                int idx = textLower.indexOf(key + ":");
                if (idx < 0) idx = textLower.indexOf(key + " :");
                if (idx >= 0) {
                    String slice = textToScan.substring(idx + (key + ":").length()).trim();

                    // Interrompi su parole di stop
                    String stopRegex = "(valori\\s+nutr|nutrition|warn|avvertenze|allergen|allergeni|conservazione|conservation|ingrediens)";
                    Pattern stopPattern = Pattern.compile(stopRegex, Pattern.CASE_INSENSITIVE);
                    Matcher stopMatcher = stopPattern.matcher(slice);
                    if (stopMatcher.find()) slice = slice.substring(0, stopMatcher.start());

                    slice = slice.replaceAll("\\s+", " ").trim();
                    if (slice.length() > 600) slice = slice.substring(0, 600);

                    return slice.isEmpty() ? null : slice;
                }
            }
        }

        return null; // nessun ingrediente trovato
    }

    // Metodo per rilevare la lingua dalle parole chiave del testo estratto
    public static String detectLanguageFromKeywords(String text) {
        if (text == null) return "I"; // default italiano

        String lowerText = text.toLowerCase();
        // Ordine di priorità lingue: italiano → inglese → francese → tedesco → spagnolo
        String[] preferredLangOrder = {"I", "GB", "F", "D", "E"};

        for (String lang : preferredLangOrder) {
            String[] keys = KEYS_BY_LANG.get(lang);
            if (keys != null) {
                for (String key : keys) {
                    if (lowerText.contains(key.toLowerCase())) {
                        return lang; // ritorna la prima lingua trovata nell'ordine
                    }
                }
            }
        }

        return "I"; // default italiano se nessuna corrispondenza
    }

    public static String detectLanguageFromKeys(String ingredients) {
        if (ingredients == null) return "I";
        String lower = ingredients.toLowerCase();
        for (Map.Entry<String, String[]> entry : KEYS_BY_LANG.entrySet()) {
            for (String key : entry.getValue()) {
                if (lower.contains(key.toLowerCase())) {
                    return entry.getKey(); // ritorna la prima lingua trovata
                }
            }
        }
        return "I"; // default italiano
    }

    // Metodo per rilevare allergeni
    public static List<String> detectAllergens(String text, String lang) {
        List<String> found = new ArrayList<>();
        if (text == null || lang == null) return found;

        String[] allergens = ALLERGENS_BY_LANG.get(lang);
        if (allergens == null) return found;

        text = text.toLowerCase();
        for (String allergen : allergens) {
            if (text.contains(allergen.toLowerCase())) {
                found.add(allergen);
            }
        }
        return found;
    }

    private IngredientExtractor() {}
}