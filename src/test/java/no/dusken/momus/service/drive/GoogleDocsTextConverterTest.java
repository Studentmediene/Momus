/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.dusken.momus.service.drive;

import no.dusken.momus.test.AbstractTestRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@Transactional
public class GoogleDocsTextConverterTest extends AbstractTestRunner {

    @Autowired
    GoogleDocsTextConverter googleDocsTextConverter;

    @Test
    public void testDefaultConverting() {
        String in = "<html><head><title>testdrive2 - Momus</title><meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\"><style type=\"text/css\">ol{margin:0;padding:0}.c2{max-width:468pt;background-color:#ffffff;padding:72pt 72pt 72pt 72pt}.c0{widows:2;orphans:2;direction:ltr}.c1{page-break-after:avoid}.c3{height:11pt}.title{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:21pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}.subtitle{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:13pt;font-family:\"Trebuchet MS\";padding-bottom:10pt;page-break-after:avoid}li{color:#000000;font-size:11pt;font-family:\"Arial\"}p{color:#000000;font-size:11pt;margin:0;font-family:\"Arial\"}h1{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:16pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h2{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:13pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h3{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:12pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h4{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;text-decoration:underline;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h5{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h6{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}</style></head><body class=\"c2\"><h1 class=\"c0 c1\"><a name=\"h.klimruoas4tf\"></a><span>Tittel</span></h1><h2 class=\"c0 c1\"><a name=\"h.wk14mrzgbisr\"></a><span>Stikktittel</span></h2><h4 class=\"c0 c1\"><a name=\"h.wlk3cuxmle39\"></a><span>Ingress</span></h4><p class=\"c0\"><span>tekst</span></p><h3 class=\"c0 c1\"><a name=\"h.i0585wht33oz\"></a><span>mellomtittel</span></h3><p class=\"c0\"><span>mere tekst</span></p><h3 class=\"c0 c1\"><a name=\"h.m6op3h3ghry3\"></a><span>ny mellomtittel</span></h3><p class=\"c0\"><span>enda mere tekst!</span></p><p class=\"c0 c3\"><span></span></p><p class=\"c0\"><span>pluss et nytt avsnitt her nede!</span></p></body></html>";

        String expectedOut = "<h1>Tittel</h1><h2>Stikktittel</h2><h4>Ingress</h4><p>tekst</p><h3>mellomtittel</h3><p>mere tekst</p><h3>ny mellomtittel</h3><p>enda mere tekst!</p><p>pluss et nytt avsnitt her nede!</p>";

        assertEquals(expectedOut, googleDocsTextConverter.convert(in));
    }

    @Test
    public void ignoreComments() {
        String in = "<html><head><title>testdrive2 - Momus</title><meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\"><style type=\"text/css\">ol{margin:0;padding:0}.c4{vertical-align:baseline;color:#000000;font-size:11pt;font-style:normal;font-family:\"Arial\";text-decoration:none;font-weight:normal}.c0{line-height:1.0;padding-top:0pt;text-align:left;direction:ltr;padding-bottom:0pt}.c3{max-width:468pt;background-color:#ffffff;padding:72pt 72pt 72pt 72pt}.c1{widows:2;orphans:2;direction:ltr}.c5{margin:5px;border:1px solid black}.c2{page-break-after:avoid}.c6{height:11pt}.title{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:21pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}.subtitle{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:13pt;font-family:\"Trebuchet MS\";padding-bottom:10pt;page-break-after:avoid}li{color:#000000;font-size:11pt;font-family:\"Arial\"}p{color:#000000;font-size:11pt;margin:0;font-family:\"Arial\"}h1{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:16pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h2{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:13pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h3{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:12pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h4{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;text-decoration:underline;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h5{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h6{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}</style></head><body class=\"c3\"><h1 class=\"c1 c2\"><a name=\"h.klimruoas4tf\"></a><span>Tittel</span></h1><p class=\"c1\"><span>tekst</span></p><h3 class=\"c1 c2\"><a name=\"h.i0585wht33oz\"></a><span>mellomtittel</span></h3><p class=\"c1\"><span>m</span><span>ere</span><sup><a href=\"#cmnt1\" name=\"cmnt_ref1\">[a]</a></sup><sup><a href=\"#cmnt2\" name=\"cmnt_ref2\">[b]</a></sup><span>&nbsp;tekst</span></p><p class=\"c1 c6\"><span></span></p><p class=\"c1\"><span>pluss et </span><span>nytt avsnitt</span><sup><a href=\"#cmnt3\" name=\"cmnt_ref3\">[c]</a></sup><span>&nbsp;her</span></p><div class=\"c5\"><p class=\"c0\"><a href=\"#cmnt_ref1\" name=\"cmnt1\">[a]</a><span class=\"c4\">bra kommentar</span></p></div><div class=\"c5\"><p class=\"c0\"><a href=\"#cmnt_ref2\" name=\"cmnt2\">[b]</a><span class=\"c4\">enig</span></p></div><div class=\"c5\"><p class=\"c0\"><a href=\"#cmnt_ref3\" name=\"cmnt3\">[c]</a><span class=\"c4\">enda en kommentar</span></p></div></body></html>";

        String expectedOut = "<h1>Tittel</h1><p>tekst</p><h3>mellomtittel</h3><p>mere tekst</p><p>pluss et nytt avsnitt her</p>";

        assertEquals(expectedOut, googleDocsTextConverter.convert(in));
    }

    @Test
    public void handleSpecialCharacters() {
        String in = "<html><head><title>testdrive2 - Momus</title><meta content=\"text/html; charset=UTF-8\" http-equiv=\"content-type\"><style type=\"text/css\">ol{margin:0;padding:0}.c1{widows:2;orphans:2;direction:ltr;page-break-after:avoid}.c2{widows:2;orphans:2;height:11pt;direction:ltr}.c3{widows:2;orphans:2;direction:ltr}.c0{max-width:468pt;background-color:#ffffff;padding:72pt 72pt 72pt 72pt}.title{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:21pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}.subtitle{widows:2;padding-top:0pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:13pt;font-family:\"Trebuchet MS\";padding-bottom:10pt;page-break-after:avoid}li{color:#000000;font-size:11pt;font-family:\"Arial\"}p{color:#000000;font-size:11pt;margin:0;font-family:\"Arial\"}h1{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:16pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h2{widows:2;padding-top:10pt;line-height:1.15;orphans:2;text-align:left;color:#000000;font-size:13pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h3{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:12pt;font-family:\"Trebuchet MS\";font-weight:bold;padding-bottom:0pt;page-break-after:avoid}h4{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;text-decoration:underline;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h5{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}h6{widows:2;padding-top:8pt;line-height:1.15;orphans:2;text-align:left;color:#666666;font-style:italic;font-size:11pt;font-family:\"Trebuchet MS\";padding-bottom:0pt;page-break-after:avoid}</style></head><body class=\"c0\"><h1 class=\"c1\"><a name=\"h.klimruoas4tf\"></a><span>Tittel</span></h1><p class=\"c3\"><span>tekst med &aelig;&oslash;&aring;</span></p><p class=\"c2\"><span></span></p><p class=\"c3\"><span>og s&aring; &uuml; og &eacute;n</span></p></body></html>";

        String expectedOut = "<h1>Tittel</h1><p>tekst med æøå</p>";

        assertEquals(expectedOut, googleDocsTextConverter.convert(in));
    }
}
