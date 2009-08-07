/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://socialsite.dev.java.net/legal/CDDL+GPL.html
 * or legal/LICENSE.txt.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at legal/LICENSE.txt.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided by Sun
 * in the GPL Version 2 section of the License file that accompanied this code.
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.socialsite.web.rest.servlets;

import com.sun.socialsite.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A Servlet which allows the web retrieval and display of images from
 * SocialSite objects.
 */
public abstract class ImageServlet extends HttpServlet {
    private static final long serialVersionUID = 0L;
    private static Log log = LogFactory.getLog(ImageServlet.class);

    private boolean handleConditionalGets = true;

    private String defaultImage;
    private Integer imageWidth;
    private Integer imageHeight;
    private String epoch = getDateFormat().format(new Date(0));

    /**
     * Public constructor.
     */
    public ImageServlet() {
    }

    @Override
    public void init(ServletConfig config)
            throws ServletException
    {
        super.init(config);

        if ((defaultImage = config.getInitParameter("default-image")) == null) {
            throw new ServletException("Missing required parameter: default-image");
        }

        String s;

        s = config.getInitParameter("handle-conditional-get");
        if (s != null) handleConditionalGets = Boolean.parseBoolean(s);

        s = config.getInitParameter("image-width");
        if (s != null) imageWidth = Integer.parseInt(s);

        s = config.getInitParameter("image-height");
        if (s != null) imageHeight = Integer.parseInt(s);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param req servlet request
     * @param resp servlet response
     */
    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String itemName = ((req.getPathInfo() != null) ? req.getPathInfo().substring(1) : null);
        if (itemName != null) itemName = URLDecoder.decode(itemName, "UTF-8");

        Result result = getResult(itemName);

        if ((result == null) || (result.imageType == null)
         || (result.imageType.equals(""))) {
            RequestDispatcher rd = req.getRequestDispatcher(defaultImage);
            rd.forward(req, resp);
            return;
        }

        long ifModifiedTime = 0L;
        if (req.getHeader("If-Modified-Since") != null) {
            try {
                ifModifiedTime = getDateFormat().parse(
                    req.getHeader("If-Modified-Since")).getTime();
            } catch (Throwable intentionallyIgnored) {}
        }
        
        // We've got an EHCache GenericResponseWrapper, it ignores date headers
        //resp.setDateHeader("Last-Modified", lastModifiedString);
        resp.setHeader("Last-Modified", getDateFormat().format(result.lastUpdate));
        resp.setHeader("ETag", result.eTag);

        // Force clients to revalidate each time
        // See RFC 2616 (HTTP 1.1 spec) secs 14.21, 13.2.1
        // We've got an EHCache GenericResponseWrapper, it ignores date headers
        //resp.setDateHeader("Expires", 0);
        resp.setHeader("Expires", epoch);

        // We may also want this (See 13.2.1 and 14.9.4)
        // response.setHeader("Cache-Control","must-revalidate");

        if (result.lastUpdate.getTime() > ifModifiedTime || !handleConditionalGets) {
            resp.setContentType(result.imageType);
            resp.setContentLength(result.imageBytes.length);
            OutputStream out = resp.getOutputStream();
            out.write(result.imageBytes, 0, result.imageBytes.length);
            out.close();
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }

        return;
    }

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
    }

    protected abstract Result getResult(String itemName) throws IOException, ServletException;

    protected class Result {

        public Date lastUpdate = new Date(0L);
        public String imageType;
        public byte[] imageBytes;
        public String eTag;

        public Result(Date lastUpdate, String imageType, byte[] imageBytes) throws IOException {
            this.lastUpdate = lastUpdate;
            this.imageType = imageType;
            this.imageBytes = imageBytes;

            if ((imageBytes != null) && (imageWidth != null) && (imageHeight != null)) {
                BufferedImage origImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                BufferedImage newImage = ImageUtil.getScaledImage(origImage, imageWidth, imageHeight);
                if (newImage != null) {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ImageIO.write(newImage, "png", byteStream);
                    this.imageBytes = byteStream.toByteArray();
                    this.imageType = "image/png";
                }
            }

            if (imageBytes != null) {
                this.eTag = "\"" + calculateChecksum(imageBytes) + "\"";
            }
            else {
                this.eTag = "\"-1\"";
            }
        }

        private String calculateChecksum(byte[] bytes) {
            Checksum checksum = new Adler32();
            checksum.update(bytes, 0, bytes.length);
            return Long.toString(checksum.getValue());
        }
    }
}
