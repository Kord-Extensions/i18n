import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
	`maven-publish`
	signing

	id("com.vanniktech.maven.publish.base")
}

val isSnapshot = project.version.toString().contains("SNAPSHOT")
val isTag = (project.findProperty("publishingTag") as String?) == "true"

afterEvaluate {
	publishing {
		repositories {
			maven {
				name = "kordEx"

				url = if (isSnapshot) {
					uri("https://repo.kordex.dev/snapshots/")
				} else {
					uri("https://repo.kordex.dev/releases/")
				}

				credentials {
					username = project.findProperty("kordexMavenUsername") as String?
						?: System.getenv("KORDEX_MAVEN_USERNAME")

					password = project.findProperty("kordexMavenPassword") as String?
						?: System.getenv("KORDEX_MAVEN_PASSWORD")
				}

				version = project.version
			}

			mavenPublishing {
				if (isTag && !isSnapshot) {
					publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, true)
				}

				configure(
					KotlinJvm(
						javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
						sourcesJar = true
					)
				)

				signAllPublications()

				coordinates(project.group.toString(), project.name, project.version.toString())

				pom {
					name.set(project.ext.get("pubName").toString())
					description.set(project.ext.get("pubDesc").toString())

					url.set("https://kordex.dev")

					packaging = "jar"

					scm {
						connection.set("scm:git:https://github.com/Kord-Extensions/i18n.git")
						developerConnection.set("scm:git:git@github.com:Kord-Extensions/i18n.git")
						url.set("https://github.com/Kord-Extensions/i18n.git")
					}

					licenses {
						license {
							name.set("Mozilla Public License Version 2.0")
							url.set("https://www.mozilla.org/en-US/MPL/2.0/")
						}
					}

					developers {
						developer {
							id.set("gdude2002")
							name.set("Gareth Coles")
						}
					}
				}
			}
		}
	}

	signing {
		val signingKey: String? by project ?: return@signing
		val signingPassword: String? by project ?: return@signing

		useInMemoryPgpKeys(signingKey, signingPassword)

		sign(publishing.publications["maven"])
	}
}

afterEvaluate {
	project.publishing.publications.forEach { publication ->
		if (publication is MavenPublication) {
			println(">> Publication: ${publication.groupId}:${publication.artifactId}:${publication.version}")

			println(
				"   Classifiers: " +
					publication.artifacts
						.filter { artifact -> artifact.classifier != null }
						.sortedBy { it.classifier }
						.joinToString { it -> "${it.classifier}:${it.extension}" }
			)

			println(
				"   Repos: " +
					project.publishing.repositories
						.sortedBy { it.name }
						.joinToString { it.name }
			)
		}
	}
}
