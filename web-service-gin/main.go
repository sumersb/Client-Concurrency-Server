package main

import (
	"encoding/json"

	"net/http"

	"github.com/gin-gonic/gin"
)

// album represents data about a record album.
type album struct {
	Artist string `json:"artist"`
	Title  string `json:"title"`
	Year   string `json:"year"`
}

// Test Album to return for Get requests
var testAlbum = album{"Shakira", "Waka waka", "2012"}

func main() {
	router := gin.Default()
	router.GET("/albums/:id", getAlbumByID)
	router.POST("/albums", postAlbums)
	router.Run(":8080")
}

// postAlbums adds an album from JSON received in the request body.
func postAlbums(c *gin.Context) {
	_, err := c.MultipartForm()
	if err != nil {
		c.IndentedJSON(http.StatusBadRequest, gin.H{"msg": "Invalid input"})
		return
	}

	// Get form values
	albumInfo := c.Request.FormValue("profile")
	var newAlbum album
	if err := json.Unmarshal([]byte(albumInfo), &newAlbum); err != nil {
		c.IndentedJSON(http.StatusBadRequest, gin.H{"msg": "Invalid input"})
		return
	}
	imageSize := 0
	// Get the image file
	_, imageHeader, err := c.Request.FormFile("image")
	if err == nil {
		imageSize = int(imageHeader.Size)
	}

	// Respond with the fixed key and image size
	c.IndentedJSON(http.StatusCreated, gin.H{"albumID": "string", "imageSize": imageSize})
}

// getAlbumByID locates the album whose ID value matches the id
// parameter sent by the client, then returns that album as a response.
func getAlbumByID(c *gin.Context) {
	c.IndentedJSON(http.StatusOK, testAlbum)
}
