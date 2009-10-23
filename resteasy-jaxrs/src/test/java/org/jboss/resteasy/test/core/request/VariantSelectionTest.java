package org.jboss.resteasy.test.core.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;


/**
 @author Pascal S. de Kloe
 */
public class VariantSelectionTest {

	@Test
	public void mostSpecific() {
		ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
		negotiation.setAcceptHeaders(Arrays.asList("text/plain"));
		negotiation.setAcceptCharsetHeaders(Arrays.asList("UTF-8"));
		negotiation.setAcceptEncodingHeaders(Arrays.asList("gzip"));
		negotiation.setAcceptLanguageHeaders(Arrays.asList("en-gb"));
		
		MediaType mediaTypeWithCharset = MediaType.valueOf("text/plain; charset=UTF-8");
		MediaType mediaType = MediaType.valueOf("text/plain");
		String encoding = "gzip";
		Locale locale = Locale.UK;

		List<Variant> available = new ArrayList<Variant>();
		available.add(new Variant(mediaTypeWithCharset, null, null));
		available.add(new Variant(mediaTypeWithCharset, locale, null));
		available.add(new Variant(mediaTypeWithCharset, null, encoding));
		available.add(new Variant(mediaTypeWithCharset, locale, encoding));
		available.add(new Variant(mediaType, null, null));
		available.add(new Variant(mediaType, locale, null));
		available.add(new Variant(mediaType, null, encoding));
		available.add(new Variant(mediaType, locale, encoding));
		available.add(new Variant(null, locale, null));
		available.add(new Variant(null, locale, encoding));
		available.add(new Variant(null, null, encoding));
		
		// Assert all acceptable:
		for (Variant variant : available)
			assertEquals(variant, negotiation.getBestMatch(Arrays.asList(variant)));

		Variant best = negotiation.getBestMatch(available);
		assertNotNull(best);
		assertEquals(mediaTypeWithCharset, best.getMediaType());
		assertEquals(encoding, best.getEncoding());
		assertEquals(locale, best.getLanguage());
	}


	@Test
	public void mostSpecificMediaType() {
		String header ="text/*, text/html, text/html;level=1, */*";
		ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
		negotiation.setAcceptHeaders(Arrays.asList(header));

		Variant o1 = new Variant(MediaType.valueOf("text/html;level=1"), null, null);
		Variant o2 = new Variant(MediaType.valueOf("text/html"), null, null);
		Variant o3 = new Variant(MediaType.valueOf("text/*"), null, null);
		Variant o4 = new Variant(MediaType.valueOf("*/*"), null, null);

		List<Variant> available = new ArrayList<Variant>();
		available.add(o4);
		assertEquals(o4, negotiation.getBestMatch(available));
		available.add(o3);
		assertEquals(o3, negotiation.getBestMatch(available));
		available.add(o2);
		assertEquals(o2, negotiation.getBestMatch(available));
		available.add(o1);
		assertEquals(o1, negotiation.getBestMatch(available));
	}


	@Test
	public void mediaTypeQualityFactor() {
		String header1 = "text/*;q=0.3, text/html;q=0.7, text/html;level=1";
		String header2 = "text/html;level=2;q=0.4, */*;q=0.5";
		ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
		negotiation.setAcceptHeaders(Arrays.asList(header1, header2));
		negotiation.setAcceptLanguageHeaders(Arrays.asList("en"));

		Variant q03 = new Variant(MediaType.valueOf("text/plain"), null, null);
		Variant q04 = new Variant(MediaType.valueOf("text/html;level=2"), null, null);
		Variant q05 = new Variant(MediaType.valueOf("image/jpeg"), null, null);
		Variant q07 = new Variant(MediaType.valueOf("text/html"), null, null);
		Variant q07plus = new Variant(MediaType.valueOf("text/html;level=3"), null, null);
		Variant q10 = new Variant(MediaType.valueOf("text/html;level=1"), null, null);

		List<Variant> available = new ArrayList<Variant>();
		available.add(q03);
		assertEquals(q03, negotiation.getBestMatch(available));
		available.add(q04);
		assertEquals(q04, negotiation.getBestMatch(available));
		available.add(q05);
		assertEquals(q05, negotiation.getBestMatch(available));
		available.add(q07);
		assertEquals(q07, negotiation.getBestMatch(available));
		available.add(q07plus);
		assertEquals(q07plus, negotiation.getBestMatch(available));
		available.add(q10);
		assertEquals(q10, negotiation.getBestMatch(available));
	}

}
