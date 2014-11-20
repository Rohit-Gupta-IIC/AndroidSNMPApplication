package mib;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.value.ObjectIdentifierValue;

public class mibReading {

	// static File file = new File("RFC1213-MIB");

	public static Mib loadMib(File file) throws MibLoaderException, IOException {

		MibLoader loader = new MibLoader();

		loader.addDir(file.getParentFile());

		return loader.load(file);

	}

	public static HashMap extractOids(Mib mib) {
		HashMap map = new HashMap();
		Iterator iter = mib.getAllSymbols().iterator();
		MibSymbol symbol;
		MibValue value;

		while (iter.hasNext()) {
			symbol = (MibSymbol) iter.next();
			value = extractOid(symbol);
			if (value != null) {
				
				String s = symbol.getName();
				String s2 = value.toString();
				map.put(s, s2);
			}
		}
		return map;
	}

	public static ObjectIdentifierValue extractOid(MibSymbol symbol) {
		MibValue value;

		if (symbol instanceof MibValueSymbol) {
			value = ((MibValueSymbol) symbol).getValue();
			if (value instanceof ObjectIdentifierValue) {
				return (ObjectIdentifierValue) value;
			}
		}
		return null;
	}

	

}
