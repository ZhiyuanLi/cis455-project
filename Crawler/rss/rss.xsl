<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/documentcollection">
	<html>
	<body>
		<h2>RSS 2.0 Aggregator</h2>
		<xsl:for-each select="document">
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="rss[@version='2.0']/channel/link"/>
				</xsl:attribute>

				<xsl:value-of select="rss[@version='2.0']/channel/title"/>
			</a>

			<h4>Matching Items</h4>
			<ul>
			<xsl:for-each select="rss[@version='2.0']/channel/item[description[contains(text(), 'war') or contains(text(), 'peace')] or title[contains(text(), 'war') or contains(text(), 'peace')]]">
				<li>
					<a>
					<xsl:value-of select="title"/>
					<xsl:attribute name="href">
						<xsl:value-of select="link"/>
					</xsl:attribute>
					</a>
					<p>
						<xsl:value-of select="description"/>
					</p>
				</li>
			</xsl:for-each>
			</ul>
		</xsl:for-each>
	</body>
	</html>
</xsl:template>

</xsl:stylesheet>