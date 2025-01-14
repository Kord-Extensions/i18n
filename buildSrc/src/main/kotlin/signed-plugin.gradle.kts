plugins {
	signing
	`maven-publish`
}

signing {
	val signingKey: String? by project ?: return@signing
	val signingPassword: String? by project ?: return@signing

	useInMemoryPgpKeys(signingKey, signingPassword)

	sign(publishing.publications)
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
