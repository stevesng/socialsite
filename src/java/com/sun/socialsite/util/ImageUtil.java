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

package com.sun.socialsite.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import javax.imageio.IIOException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Image Utility.
 */
public class ImageUtil {

    private static Log log = LogFactory.getLog(ImageUtil.class);
    private static int maxWidth = 150;
    private static int maxHeight = 150;

    private HttpConnectionManager httpConnectionManager;
    private Protocol httpsProtocol;

    /**
     * Public constructor.
     */
    public ImageUtil(int timeout) {

        httpConnectionManager = new MultiThreadedHttpConnectionManager();
        httpConnectionManager.getParams().setConnectionTimeout(timeout);
        httpConnectionManager.getParams().setSoTimeout(timeout);

        // This allows us to accept self-signed certs
        // TODO: make this configurable, or at least stop setting default for all HttpClients
        ProtocolSocketFactory protocolSocketFactory = new LeniantSSLProtocolSocketFactory();
        httpsProtocol = new Protocol("https", protocolSocketFactory, 443);
        Protocol.registerProtocol("https", httpsProtocol);
    }

    /**
     * Grabs an image from a URI (and scales it to some reasonable size).
     *
     * @param uriString the URI from which to grab the image.
     */
    public BufferedImage getImage(final String uriString) {

        // TODO: better approach to Exceptions

        if ((uriString == null) || ("".equals(uriString))) {
            return null;
        }

        URI uri = null;
        GetMethod method = null;
        try {
            uri = new URI(uriString, false);
            HttpClient httpClient = new HttpClient(httpConnectionManager);
            method = new GetMethod(uri.getEscapedURI());
            httpClient.executeMethod(method);
            BufferedImage origImage = ImageIO.read(method.getResponseBodyAsStream());
            log.debug(String.format("Got origImage for %s", uriString));
            return getScaledImage(origImage, maxWidth, maxHeight);
        } catch (ConnectException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (ConnectTimeoutException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (SocketTimeoutException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (UnknownHostException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (IllegalArgumentException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (IllegalStateException e) {
            log.warn(String.format("Failed to retrieve image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (IIOException e) {
            log.warn(String.format("Failed to process image: %s [%s]", uriString, e.toString()));
            return null;
        } catch (Throwable t) {
            log.error(String.format("Failed to retrieve image: %s", uriString), t);
            return null;
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    public static BufferedImage getScaledImage(BufferedImage origImage, Integer desiredWidth, Integer desiredHeight)
            throws IOException {

        if (origImage == null) {
            return null;
        }

        if (desiredWidth == null) {
            desiredWidth = origImage.getWidth();
        }

        if (desiredHeight == null) {
            desiredHeight = origImage.getHeight();
        }

        int origWidth = origImage.getWidth();
        int origHeight = origImage.getHeight();

        double ratio = Math.min((((double)desiredWidth)/origWidth), (((double)desiredHeight)/origHeight));

        int extraWidth = desiredWidth-((int)(origWidth*ratio));
        int extraHeight = desiredHeight-((int)(origHeight*ratio));

        int tmpWidth = (desiredWidth-extraWidth);
        int tmpHeight = (desiredHeight-extraHeight);
        BufferedImage tmpImage = getScaledInstance(origImage, tmpWidth, tmpHeight, RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);

        log.debug(String.format("tmpImage[width=%d height=%d", tmpImage.getWidth(), tmpImage.getHeight()));

        if ((tmpImage.getWidth() == desiredWidth) && (tmpImage.getHeight() == desiredHeight)) {
            return tmpImage;
        } else {
            BufferedImage scaledImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // We recalculate these in case scaling didn't quite hit its targets
            extraWidth = desiredWidth-tmpImage.getWidth();
            extraHeight = desiredHeight-tmpImage.getHeight();

            int dx1 = extraWidth/2;
            int dy1 = extraHeight/2;
            int dx2 = desiredWidth-dx1;
            int dy2 = desiredWidth-dy1;

            // transparent background
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, desiredWidth, desiredHeight);

            g2d.drawImage(tmpImage, dx1, dy1, dx2, dy2, null);
            return scaledImage;
        }
    }

    /**
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * NOTE: Adapted from code at
     * {@link http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
    private static BufferedImage getScaledInstance(BufferedImage img,
                                                   int targetWidth,
                                                   int targetHeight,
                                                   Object hint,
                                                   boolean higherQuality)
    {
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage)img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
        } while (w > targetWidth || h > targetHeight);

        return ret;
    }

}
