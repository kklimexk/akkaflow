package pl.edu.agh.examples

import java.net.URL

import scala.io.Source
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class PathwaysTest extends FunSuite with BeforeAndAfterAll {

  var id: Int = _
  var convertId: String = _
  var pathWayByGene: String = _
  var pathWayEntry: String = _
  var pathWayImage: URL = _
  var geneDescription: String = _

  override def beforeAll {
    id = 948873

    convertId = Source.fromURL("http://rest.kegg.jp/conv/genes/ncbi-geneid:" + id).mkString
      .trim
      .split("\t")(1)
    pathWayByGene = Source.fromURL("http://rest.kegg.jp/link/pathway/" + convertId).mkString
      .trim
      .split("\t")(1)
      .split("path:")(1)
    pathWayEntry = Source.fromURL("http://rest.kegg.jp/get/" + pathWayByGene).mkString
    pathWayImage = new URL("http://rest.kegg.jp/get/" + pathWayByGene + "/image")
    geneDescription = Source.fromURL("http://rest.kegg.jp/find/genes/" + convertId).mkString
      .trim
      .split("\t")(1)
  }

  test("Should return true when ncbi-geneid converted to eco id") {
    assert(convertId equals "eco:b4368")
  }

  test("Path of given id should equals to eco00970") {
    assert(pathWayByGene equals "eco00970")
  }

  test("Pathway entry should not be empty") {
    assert(pathWayEntry.nonEmpty)
  }

  test("Pathway image should not be null") {
    assert(pathWayImage != null)
  }

  test("Gene description should not be empty") {
    assert(geneDescription.nonEmpty)
  }
}
