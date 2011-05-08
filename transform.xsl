<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-
transitional.dtd" 
  doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes" encoding="UTF-8"/><!--should be: method="xml"-->

<xsl:preserve-space elements="span"/>

<xsl:template match="/domus/choices">
    <html>
        <head>
            <title>Domus</title>
            <link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/base/base-min.css" />
        </head>
        <body>
            <h1>Lists</h1>
                <xsl:apply-templates select="list"/>
        </body>
    </html>
</xsl:template>

<xsl:template match="list">
    <p><xsl:value-of select="@name" /></p>
    <ol>
        <xsl:for-each select="room">
            <li><p>
                <xsl:value-of select="@building" />
                : <!-- without this colon, there would be no space between the elements. (why?) -->
                <xsl:value-of select="@number" />
            </p></li>
        </xsl:for-each>
    </ol>
</xsl:template>
</xsl:stylesheet>
